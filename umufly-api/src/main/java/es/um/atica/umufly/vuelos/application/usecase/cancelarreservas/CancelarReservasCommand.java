package es.um.atica.umufly.vuelos.application.usecase.cancelarreservas;

import java.util.UUID;

import es.um.atica.fundewebjs.umubus.domain.cqrs.SyncCommand;
import es.um.atica.umufly.vuelos.domain.model.DocumentoIdentidad;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;

public class CancelarReservasCommand extends SyncCommand<ReservaVuelo> {

	private DocumentoIdentidad documentoIdentidadTitular;
	private UUID idVuelo;

	public CancelarReservasCommand( DocumentoIdentidad documentoIdentidadTitular, UUID idVuelo ) {
		this.documentoIdentidadTitular = documentoIdentidadTitular;
		this.idVuelo = idVuelo;
	}

	public static CancelarReservasCommand of( DocumentoIdentidad documentoIdentidadTitular, UUID idVuelo ) {
		return new CancelarReservasCommand( documentoIdentidadTitular, idVuelo );
	}

	public DocumentoIdentidad getDocumentoIdentidadTitular() {
		return documentoIdentidadTitular;
	}

	public UUID getIdVuelo() {
		return idVuelo;
	}

}
