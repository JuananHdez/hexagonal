package es.um.atica.umufly.parking.domain.model;

public record Importe( double valor ) {

	public Importe {
		if ( valor <= 0 ) {
			throw new IllegalArgumentException( "El importe debe ser mayor que 0" );
		}
	}
}

