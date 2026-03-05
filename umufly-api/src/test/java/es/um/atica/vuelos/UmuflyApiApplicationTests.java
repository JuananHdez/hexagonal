package es.um.atica.vuelos;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import jakarta.persistence.Entity;
import jakarta.validation.Valid;

@AnalyzeClasses( packages = "es.um.atica.umufly" )
class UmuflyApiApplicationTests {

	@ArchTest
	static final ArchRule ninguna_interfaz_acaba_en_impl = classes().that().areInterfaces().should().haveSimpleNameNotEndingWith( "Impl" ).because( "Las interfaces no deben terminar en impl." );

	@ArchTest
	static final ArchRule codigo_respeta_arquitectura_hexagonal = layeredArchitecture()// Define una estructura por capas
	.consideringAllDependencies() // Cojo todas las dependencias del proyecto
	.layer("Domain").definedBy("..domain..") // Defino la capa de dominio por todas las clases dentro de ..domain..
	.layer( "Application" ).definedBy( "..application.." ) // Defino la capa de aplicacion por todas las clases dentro de application..
	.layer("Adapters").definedBy("..adaptors..")//Defino la capa de adaptadores por todas las clases dentro de ..adaptors..
	.whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapters") // Indico que "imports" de la
	// capa de dominio pueden estar en Application y Adapters
	.whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters")// Indico que "imports" de la capa de
	// adaptadores pueden estar en Application (pero no en dominio)
	.whereLayer("Adapters").mayNotBeAccessedByAnyLayer(); // Ninguna capa debe tener import de adapters

	private static final ArchCondition<JavaMethod> METODO_REST_VALIDA_PARAMETROS = new ArchCondition<>(
			"Rest debe tener @Valid o @Validated en parámetros @RequestBody") {
		@Override
		public void check(JavaMethod metodo, ConditionEvents events) {
			boolean validaParametro = false;
			for (JavaParameter parametro : metodo.getParameters()) {
				//Compruebo si el metodo tiene RequestBody (JSON)
				if (parametro.isAnnotatedWith(RequestBody.class)) {
					validaParametro = parametro.isAnnotatedWith(Valid.class)
							|| parametro.isAnnotatedWith(Validated.class)
							|| metodo.getOwner().isAnnotatedWith(Validated.class);
					if (!validaParametro) {
						String message = String.format("El método %s tiene un @RequestBody sin validación",
								metodo.getFullName());
						//Aniado un evento de violacion
						events.add(SimpleConditionEvent.violated(metodo, message));
					}
				}
			}
		}
	};

	@ArchTest
	static final ArchRule api_rest_debe_validar_datos_entrada = methods().that().areDeclaredInClassesThat().areAnnotatedWith( RestController.class ).and().arePublic().should( METODO_REST_VALIDA_PARAMETROS );

	private static final DescribedPredicate<JavaClass> IMPLEMENTA_ALGUNA_INTERFAZ = new DescribedPredicate<JavaClass>( "implementa al menos una interfaz" ) {

		@Override
		public boolean test( JavaClass javaClass ) {
			return !javaClass.getInterfaces().isEmpty();
		}

	};

	@ArchTest
	static final ArchRule toda_implementacion_acaba_en_impl = classes().that().areInterfaces().and().areNotEnums().and( IMPLEMENTA_ALGUNA_INTERFAZ ).should().haveSimpleNameEndingWith( "Impl" );

	@ArchTest
	static final ArchRule dto_acaba_en_dto = classes().that().resideInAnyPackage( "..dto" ).and().areNotEnums().should().haveSimpleNameEndingWith( "DTO" ).because( "Los DTO deben terminar en DTO." );

	@ArchTest
	static final ArchRule RestController_solo_capa_adptadores = classes().that().areAnnotatedWith( RestController.class ).should().resideInAnyPackage( "..adaptors.." ).because( "Los RestController solo pueden estar en la capa de adaptadores." );

	@ArchTest
	static final ArchRule ninguna_clase_mas_de_20_metodos_publicos = classes().that().areNotAnnotatedWith( Entity.class ).should( new ArchCondition<JavaClass>( "tener como máximo 20 métodos públicos" ) {

		@Override
		public void check( JavaClass javaClass, ConditionEvents events ) {

			long publicMethods = javaClass.getMethods().stream().filter( m -> m.getModifiers().contains( com.tngtech.archunit.core.domain.JavaModifier.PUBLIC ) ).count();

			boolean cumple = publicMethods <= 20;

			events.add( new SimpleConditionEvent( javaClass, cumple, javaClass.getName() + " tiene " + publicMethods + " métodos públicos" ) );
		}
	} ).because( "Ninguna clase debe tener más de 20 métodos públicos." );

}
