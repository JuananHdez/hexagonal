package es.um.atica.umufly.vuelos.application.usecase.listarreservas;

import org.springframework.data.domain.Page;

import es.um.atica.fundewebjs.umubus.domain.cqrs.Query;
import es.um.atica.umufly.vuelos.domain.model.DocumentoIdentidad;
import es.um.atica.umufly.vuelos.domain.model.ReservaVuelo;

public class ListarReservasQuery extends Query<Page<ReservaVuelo>> {

	private DocumentoIdentidad documentoIdentidad;
	private int pagina;
	private int tamanioPagina;

	public ListarReservasQuery( DocumentoIdentidad documentoIdentidad, int pagina, int tamanioPagina ) {
		this.documentoIdentidad = documentoIdentidad;
		this.pagina = pagina;
		this.tamanioPagina = tamanioPagina;
	}

	public static ListarReservasQuery of( DocumentoIdentidad documentoIdentidad, int pagina, int tamanioPagina ) {
		return new ListarReservasQuery( documentoIdentidad, pagina, tamanioPagina );
	}

	public DocumentoIdentidad getDocumentoIdentidad() {
		return documentoIdentidad;
	}

	public int getPagina() {
		return pagina;
	}

	public int getTamanioPagina() {
		return tamanioPagina;
	}


}
