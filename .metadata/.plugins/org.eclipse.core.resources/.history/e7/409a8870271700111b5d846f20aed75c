package es.um.atica.umufly.vuelos.application.usecase.obtenervuelos;

import java.util.UUID;

import es.um.atica.fundewebjs.umubus.domain.cqrs.QueryHandler;
import es.um.atica.umufly.vuelos.application.dto.VueloAmpliadoDTO;
import es.um.atica.umufly.vuelos.application.mapper.ApplicationMapper;
import es.um.atica.umufly.vuelos.application.port.ReservasVueloReadRepository;
import es.um.atica.umufly.vuelos.application.port.VuelosReadRepository;
import es.um.atica.umufly.vuelos.domain.model.Vuelo;

public class ObtenerVuelosQueryHandler implements QueryHandler<VueloAmpliadoDTO, ObtenerVuelosQuery> {

	private final VuelosReadRepository vueloReadRepository;
	private final ReservasVueloReadRepository reservasVueloReadRepository;

	public ObtenerVuelosQueryHandler( VuelosReadRepository vueloReadRepository, ReservasVueloReadRepository reservasVueloReadRepository ) {
		this.vueloReadRepository = vueloReadRepository;
		this.reservasVueloReadRepository = reservasVueloReadRepository;
	}

	@Override
	public VueloAmpliadoDTO handle( ObtenerVuelosQuery query ) throws Exception {
		Vuelo vuelo = vueloReadRepository.findVuelo( query.getIdReserva() );
		UUID vueloReserva = query.getDocumentoIdentidad() != null ? reservasVueloReadRepository.findReservaIdByVueloIdAndPasajero( query.getDocumentoIdentidad(), vuelo.getId() ) : null;

		return ApplicationMapper.vueloToDTO( vuelo, vueloReserva );
	}

}
