
# 🛠️ Práctica 5 — Eventos: Ordenando el tiempo

## 🎯 Objetivo

En esta práctica vamos a evolucionar el proyecto para incorporar **CQRS asíncrono y eventos** utilizando la librería **UMUBUS**.

Hasta ahora los casos de uso se ejecutaban de forma síncrona.  
A partir de esta práctica:

- Algunos comandos pasarán a ser asíncronos  
- Introduciremos eventos de intención  
- Implementaremos eventos de resultado (OK / KO)  
- Separaremos claramente:
  - Commands  
  - Queries  
  - Eventos de integración entre contextos  

---

# 📌 Hito 0 — Preparar el proyecto para CQRS asíncrono

Antes de empezar a crear eventos, debemos preparar el proyecto.

No todo debe ser asíncrono.  
Primero debemos decidir qué operaciones lo necesitan.

- Determinar qué comandos serán síncronos y cuáles asíncronos  
- Adaptar los endpoints REST para usar `QueryBus` y `CommandBus` (El controlador ya no ejecuta directamente la lógica, sino que delega en la infraestructura de mensajería.) 
- Modificar los agregados raíz para extender de `AggregateRoot` (Esto permitirá que el agregado acumule eventos que serán publicados posteriormente por el bus.) 

### Criterios a aplicar:

- Operaciones que pueden depender de sistemas externos → **Asíncrono**  
- Operaciones de lectura → **Query síncrona**  
- Operaciones rápidas y puramente locales → podrían ser **SyncCommand**  

---

<details>
<summary>📎 Resultado esperado</summary>

Analizamos los casos de uso:

| Caso de uso | Tipo recomendado |
|-------------|-----------------|
| CrearReservaVuelo | Síncrono (Podría ser asíncrono) |
| CancelarReservaVuelo | Asíncrono |
| CrearParking | Síncrono (Podría ser asíncrono) |
| CancelarParking | Asíncrono |
| Consultas (listados, tarifas, etc.) | Query síncrona |

### Adaptamos controladores REST

**ANTES:**

```java
crearParkingCommandHandler.handle(…);
```

**AHORA:**

```java
syncBus.handle(…);
// o
bus.handle(…);
```

### Extender de AggregateRoot

**ANTES:**

```java
public class ReservaVuelo {
```

**AHORA:**

```java
public class ReservaVuelo extends AggregateRoot {
```

</details>

---

# 📌 Hito 1 — Eventos de intención

- Determinar los eventos de intención  
- Implementar los eventos correspondientes  
  - `es.um.atica.umufly.parking.domain.event`
  - `es.um.atica.umufly.vuelos.domain.event`
- Implementar los consumidores asociados  
  - `es.um.atica.umufly.parking.adaptors.consumers`
  - `es.um.atica.umufly.vuelos.adaptors.consumers`
- Añadir los eventos donde corresponda en el agregado raíz  
- Añadir el bus en los `commandHandler` oportunos y publicar los eventos  

---

<details>
<summary>📎 Resultado esperado</summary>

Implementar eventos que representen la intención de ejecutar una operación:

- CrearReservaVueloEvent  
- CrearReservaVueloConsumer  
- CancelarReservaVueloEvent  
- CancelarReservaVueloConsumer  
- CrearParkingEvent  
- CrearParkingConsumer  
- CancelarParkingEvent  
- CancelarParkingConsumer  

Añadir los eventos donde corresponda en el agregado raíz:

- `ReservaVuelo.solicitarReserva(…)`
- `ReservaVuelo.cancelarReserva(…)`
- `ReservaParking.solicitarParking(…)`
- `ReservaParking.cancelarParking(…)`

</details>

---

# 📌 Hito 2 — Eventos de resultado

En un modelo asíncrono ya no podemos devolver excepciones directamente.

Debemos notificar el resultado mediante eventos.

- Determinar los eventos de resultado  
- Implementar los eventos correspondientes  
- Implementar los consumidores asociados  
- Añadir los eventos donde corresponda (consumidores o handlers) y publicarlos  

---

<details>
<summary>📎 Resultado esperado</summary>

Implementar eventos que representen el resultado de las operaciones:

- CrearReservaVueloOKEvent  
- CrearReservaVueloOKConsumer  
- CrearReservaVueloKOEvent  
- CrearReservaVueloKOConsumer  
- CancelarReservaVueloOKEvent  
- CancelarReservaVueloOKConsumer  
- CancelarReservaVueloKOEvent  
- CancelarReservaVueloKOConsumer  
- CrearParkingOKEvent  
- CrearParkingOKConsumer  
- CrearParkingKOEvent  
- CrearParkingKOConsumer  
- CancelarParkingOKEvent  
- CancelarParkingOKConsumer  
- CancelarParkingKOEvent  
- CancelarParkingKOConsumer  

Añadir los eventos donde corresponda:

- Cuando se produzcan excepciones  
- En los consumidores de los eventos de intención  

</details>

---

# 📌 Hito 3 — Eventos para Queries de soporte

No todo son comandos.

En el caso de parking, necesitamos obtener el precio de la tarifa.

Aquí debemos reflexionar: ¿Es esto un evento o una consulta?

Para esta práctica lo modelaremos como evento con respuesta.

- Determinar los eventos de consulta de información del dominio  
- Implementar los eventos correspondientes  
- Implementar los consumidores asociados  

---

<details>
<summary>📎 Resultado esperado</summary>

Implementar eventos necesarios para obtener datos desde el dominio hacia la capa de infraestructura (el consumidor debe devolver información):

- ObtenerTarifaTipoEstacionamientoEvent  
- ObtenerTarifaTipoEstacionamientoConsumer

</details>

---

# 📌 Hito 4 — Eventos de integración entre contextos (Request / Reply)

El contexto de Parking necesita saber si un cliente tiene reserva activa en Vuelos.

No podemos depender directamente del otro contexto. Debemos comunicarnos mediante eventos o realizar proyecciones de la información que necesitamos.

- Determinar los eventos de integración entre contextos  
- Implementar los eventos correspondientes  
- Implementar los consumidores asociados  

---

<details>
<summary>📎 Resultado esperado</summary>

Implementar eventos necesarios para obtener datos desde un contexto hacia otro (el consumidor debe devolver información):

- TieneClienteReservaEvent  
- TieneClienteReservaConsumer  
- CalculaImporteParkingEvent
- CalculaImporteParkingConsumer

</details>

---

# 📌 Hito 5 — Continuar con el contexto de “parking”

Una vez integrado el modelo de eventos, continuamos con la implementación del contexto de parking.

### Implementación:

- Crear estructura de paquetes del contexto  
- Implementar el modelo del dominio  
- Implementar comandos y queries  
- Implementar handlers de los comandos y queries identificados  
- Implementar puertos  
- Implementar adaptadores, assembler, mappers y entidades de datos  
- Implementar controladores REST  

---

<details>
<summary>📎 Resultado esperado</summary>

Contexto de parking bien definido e implementado, estructurado con arquitectura hexagonal e incorporando CQRS y eventos locales.

</details>
