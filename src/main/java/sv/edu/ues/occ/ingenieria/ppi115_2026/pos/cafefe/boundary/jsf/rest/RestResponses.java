package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.text.Normalizer;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author mil
 */
public final class RestResponses {

    private RestResponses() {
    }

    public static Response conflicto(String mensaje) {
        return Response.status(Response.Status.CONFLICT)
                .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), ascii(mensaje))
                .entity(Map.of("error", mensaje))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response badRequest(String mensaje) {
        return Response.status(Response.Status.BAD_REQUEST)
                .header(ResponseHeaders.WRONG_PARAMETER.toString(), ascii(mensaje))
                .entity(Map.of("error", mensaje))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response notFound(UUID id) {
        return Response.status(Response.Status.NOT_FOUND)
                .header(ResponseHeaders.NOT_FOUND.toString(), "id: " + id)
                .build();
    }

    public static Response serverError(Exception ex) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .header(ResponseHeaders.PROCESS_ERROR.toString(), ascii(String.valueOf(ex.getMessage())))
                .build();
    }

    public static String ascii(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}