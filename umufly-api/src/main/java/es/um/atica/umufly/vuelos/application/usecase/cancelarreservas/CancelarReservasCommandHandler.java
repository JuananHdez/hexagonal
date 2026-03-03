package es.um.atica.umufly.vuelos.application.usecase.cancelarreservas;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import es.um.atica.fundewebjs.umubus.domain.cqrs.SyncCommandHandler;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloWritePort;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloWriteRepository;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;

public class CancelarReservasCommandHandler implements SyncCommandHandler<ReservaVuelo, CancelarReservasCommand> {

	private final ReservasVueloReadRepository reservasVueloReadRepository;
	private final ReservasVueloWriteRepository reservasVueloWriteRepository;
	private final ReservasVueloWritePort reservasVueloWritePort;
	private final Clock clock;

	public CancelarReservasCommandHandler( ReservasVueloReadRepository reservasVueloReadRepository, ReservasVueloWriteRepository reservasVueloWriteRepository, ReservasVueloWritePort reservasVueloWritePort, Clock clock ) {
		this.reservasVueloReadRepository = reservasVueloReadRepository;
		this.reservasVueloWriteRepository = reservasVueloWriteRepository;
		this.reservasVueloWritePort = reservasVueloWritePort;
		this.clock = clock;
	}

	@Override
	public ReservaVuelo handle( CancelarReservasCommand command ) throws Exception {
		// 1. Recuperamos la reserva
		ReservaVuelo reservaVuelo = reservasVueloReadRepository.findReservaById( command.getDocumentoIdentidadTitular(), command.getIdVuelo() );

		// 2. Cancelamos la reserva en el fronOffice
		reservaVuelo.cancelarReserva( LocalDateTime.now( clock ) );
		reservasVueloWriteRepository.cancelReserva( reservaVuelo.getId() );

		// 3. Cancelamos la reserva llamando al backoffice para que se haga eco de la cancelacion
		UUID idReservaFormalizada = reservasVueloReadRepository.findIdFormalizadaByReservaById( command.getIdVuelo() );
		reservasVueloWritePort.cancelarReservaVuelo( command.getDocumentoIdentidadTitular(), idReservaFormalizada );

		return reservaVuelo;
	}

}
