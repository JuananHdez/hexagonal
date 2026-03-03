package es.um.atica.umufly.vuelos.application.usecase.listarVuelos;

import org.springframework.data.domain.Page;

import es.um.atica.fundewebjs.umubus.domain.cqrs.Query;
import es.um.atica.umufly.vuelos.application.dto.VueloAmpliadoDTO;
import es.um.atica.umufly.vuelos.domain.model.DocumentoIdentidad;

public class ListarVuelosQuery extends Query<Page<VueloAmpliadoDTO>> {

	private DocumentoIdentidad documentoIdentidad;
	private int pagina;
	private int tamanioPagina;

	public ListarVuelosQuery( DocumentoIdentidad documentoIdentidad, int pagina, int tamanioPagina ) {
		this.documentoIdentidad = documentoIdentidad;
		this.pagina = pagina;
		this.tamanioPagina = tamanioPagina;
	}

	public static ListarVuelosQuery of( DocumentoIdentidad documentoIdentidadPasajero, int pagina, int tamanioPagina ) {
		return new ListarVuelosQuery( documentoIdentidadPasajero, pagina, tamanioPagina );
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
