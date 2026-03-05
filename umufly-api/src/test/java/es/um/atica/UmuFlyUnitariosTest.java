package es.um.atica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import es.um.atica.umufly.vuelos.domain.model.Avion;
import es.um.atica.umufly.vuelos.domain.model.ClaseAsientoReserva;
import es.um.atica.umufly.vuelos.domain.model.CorreoElectronico;
import es.um.atica.umufly.vuelos.domain.model.DocumentoIdentidad;
import es.um.atica.umufly.vuelos.domain.model.EstadoReserva;
import es.um.atica.umufly.vuelos.domain.model.EstadoVuelo;
import es.um.atica.umufly.vuelos.domain.model.Itinerario;
import es.um.atica.umufly.vuelos.domain.model.Nacionalidad;
import es.um.atica.umufly.vuelos.domain.model.NombreCompleto;
import es.um.atica.umufly.vuelos.domain.model.Pasajero;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;
import es.um.atica.umufly.vuelos.domain.model.TipoDocumento;
import es.um.atica.umufly.vuelos.domain.model.TipoVuelo;
import es.um.atica.umufly.vuelos.domain.model.Vuelo;

@TestClassOrder( ClassOrderer.OrderAnnotation.class )
public class UmuFlyUnitariosTest {

	// -- Conjunto de datos
	private static final LocalDateTime SALIDA = LocalDateTime.of( 2025, 6, 15, 10, 0 );
	private static final LocalDateTime LLEGADA = LocalDateTime.of( 2025, 6, 15, 12, 0 );
	private static final LocalDateTime ANTES_DE_SALIDA = SALIDA.minusHours( 2 );
	private static final LocalDateTime DESPUES_DE_SALIDA = SALIDA.plusMinutes( 30 );

	// -- Variables para el test
	DocumentoIdentidad titular;
	Vuelo vueloPendiente;
	Vuelo vueloCancelado;
	Vuelo vueloCompletado;
	Pasajero pasajero;

	@BeforeEach
	void setUp() {
		titular = new DocumentoIdentidad( TipoDocumento.NIF, "12345678Z" );
		pasajero = Pasajero.of( titular, new NombreCompleto( "Juan", "García", "López" ), new CorreoElectronico( "juan@ejemplo.com" ), new Nacionalidad( "Española" ) );
		Itinerario itinerario = new Itinerario( SALIDA, LLEGADA, "MAD", "BCN" );
		Avion avion = new Avion( 180 );
		vueloPendiente = Vuelo.of( UUID.randomUUID(), itinerario, TipoVuelo.NACIONAL, EstadoVuelo.PENDIENTE, avion );
		vueloCancelado = Vuelo.of( UUID.randomUUID(), itinerario, TipoVuelo.NACIONAL, EstadoVuelo.CANCELADO, avion );
		vueloCompletado = Vuelo.of( UUID.randomUUID(), itinerario, TipoVuelo.NACIONAL, EstadoVuelo.COMPLETADO, avion );

	}

	@Nested
	@DisplayName( "cancelarReserva_correcta" )
	@Order( 1 )
	class CuandoCancelar {

		@Test
		void formalizar_reserva_pasa_a_estado_activa() {
			ReservaVuelo reserva = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, SALIDA.minusDays( 5 ), 0, 10 );
			reserva.formalizarReserva();
			assertEquals( EstadoReserva.ACTIVA, reserva.getEstado() );
		}

		@Test
		void crear_reserva_en_pendiente() {
			ReservaVuelo reserva = ReservaVuelo.solicitarReserva(titular, pasajero, vueloPendiente,
					ClaseAsientoReserva.ECONOMICA, SALIDA.minusDays( 5 ), 0, 10);
			assertEquals( EstadoReserva.PENDIENTE, reserva.getEstado() );
		}

		@Test
		void toda_reserva_debe_tener_id_unico() {
			ReservaVuelo reserva1 = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, SALIDA.minusDays( 5 ), 0, 10 );
			ReservaVuelo reserva2 = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, SALIDA.minusDays( 4 ), 0, 11 );
			assertNotEquals( reserva1.getId(), reserva2.getId() );
		}

		@Test
		void reserva_valida_contiene_todos_los_datos() {
			// Arrange: crear la reserva
			ReservaVuelo reserva = ReservaVuelo.solicitarReserva( titular, pasajero, vueloPendiente, ClaseAsientoReserva.ECONOMICA, SALIDA.minusDays( 5 ), 0, 10 );

			// Assert: verificar que todos los datos esenciales están presentes
			assertNotEquals( null, reserva.getId() );
			assertNotEquals( null, reserva.getIdentificadorTitular() );
			assertNotEquals( null, reserva.getPasajero() );
			assertNotEquals( null, reserva.getVuelo() );
			assertNotEquals( null, reserva.getFechaReserva() );
			assertNotEquals( null, reserva.getEstado() );

			// Opcional: validar que la fecha de reserva sea antes de la salida
			// assertTrue( reserva.getFechaReserva().isBefore( reserva.getVuelo().getItinerario().salida() ), "La fecha de reserva
			// debe ser antes de la salida del vuelo" );
		}



	}

}
