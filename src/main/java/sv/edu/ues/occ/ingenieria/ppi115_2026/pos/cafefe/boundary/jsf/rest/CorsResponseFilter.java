package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;


import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author mil
 */
// Habilita CORS para que el frontend, servido desde otro origen, consuma el API.

@Provider
@PreMatching
public class CorsResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String METODOS_PERMITIDOS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    // 30 minutos para el cache
    public static final int MAXIMO_CACHE = 30 * 60;
    public static final String CABECERAS_PERMITIDAS = "origin, accept, content-type";
    // Se exponen todos los encabezados de ResponseHeaders (sin ellos el navegador no puede leer
    // Not-Found, Violates-Business-Rules, etc.) y Location
    public static final String CABECERAS_EXPUESTAS = Arrays.stream(ResponseHeaders.values())
            .map(ResponseHeaders::toString)
            .distinct()
            .collect(Collectors.joining(", ")) + ", Location";

    // La solicitud se responde con 200, sin depender de que
    // el servidor genere un OPTIONS automático para cada ruta.
    @Override
    public void filter(ContainerRequestContext peticion) {
        if ("OPTIONS".equalsIgnoreCase(peticion.getMethod())
                && peticion.getHeaderString("Origin") != null
                && peticion.getHeaderString("Access-Control-Request-Method") != null) {
            peticion.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext peticion, ContainerResponseContext respuesta) {
        MultivaluedMap<String, Object> h = respuesta.getHeaders();
        // putSingle evita encabezados duplicados si otro filtro ya los puso
        h.putSingle("Access-Control-Allow-Origin", "*");
        h.putSingle("Access-Control-Allow-Headers", cabecerasPermitidas(peticion));
        h.putSingle("Access-Control-Expose-Headers", CABECERAS_EXPUESTAS);
        h.putSingle("Access-Control-Allow-Methods", METODOS_PERMITIDOS);
        h.putSingle("Access-Control-Max-Age", MAXIMO_CACHE);
        //sin "Allow-Credentials: true"; con origen "*" el navegador lo rechaza y el API no usa cookies.
    }

    // El navegador anuncia qué encabezados quiere usar en "Access-Control-Request-Headers"
    String cabecerasPermitidas(ContainerRequestContext peticion) {
        String pedidas = peticion.getHeaderString("Access-Control-Request-Headers");
        return (pedidas == null || pedidas.isBlank())
                ? CABECERAS_PERMITIDAS
                : CABECERAS_PERMITIDAS + ", " + pedidas;
    }
}