package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Pago;

import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.badRequest;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.conflicto;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.notFound;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.serverError;

/**
 *
 * @author mil
 */
@Path("pago")
public class PagoResource {

    private static final Logger LOG = Logger.getLogger(PagoResource.class.getName());
    private static final int ESTADO_MAX = 20;

    @Inject
    PagoRepository pagoRepository;

    @Inject
    FacturaRepository facturaRepository;

    /**
     * Con ?idFactura=devuelve solo los de esa factura.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max,
            @QueryParam("idFactura") UUID idFactura) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<Pago> pagos;
                Long total;
                if (idFactura != null) {
                    pagos = pagoRepository.findByFactura(idFactura, first, max);
                    total = pagoRepository.countByFactura(idFactura);
                } else {
                    pagos = pagoRepository.findRange(first, max);
                    total = pagoRepository.count();
                }
                return Response.ok(pagos)
                        .header(ResponseHeaders.TOTAL_REGISTROS.toString(), total)
                        .build();
            }
            return badRequest("Los parametros 'first' debe ser >= 0 y 'max' debe ser > 0 y <= 50");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return serverError(ex);
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id == null) {
            return badRequest("id nulo");
        }
        try {
            Pago encontrado = pagoRepository.findById(id);
            if (encontrado != null) {
                return Response.ok(encontrado).build();
            }
            return notFound(id);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return serverError(ex);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(PagoRequest request, @Context UriInfo uriInfo) {
        if (request == null || request.idFactura == null) {
            return badRequest("idFactura es obligatorio");
        }
        String estado = request.estado != null ? request.estado.trim() : null;
        if (estado != null && estado.length() > ESTADO_MAX) {
            return badRequest("El estado no puede superar " + ESTADO_MAX + " caracteres");
        }
        try {
            Factura factura = facturaRepository.findById(request.idFactura);
            if (factura == null) {
                return conflicto("La factura no existe: " + request.idFactura);
            }

            Pago nuevo = new Pago(UUID.randomUUID());
            nuevo.setIdFactura(factura);
            nuevo.setEstado(estado);
            nuevo.setObservaciones(request.observaciones != null ? request.observaciones : "");
            pagoRepository.crear(nuevo);

            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(nuevo.getIdPago()))
                    .build();
            return Response.created(location).build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return serverError(ex);
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@PathParam("id") UUID id, PagoRequest request) {
        if (id == null || request == null) {
            return badRequest("ID o cuerpo nulo");
        }
        try {
            Pago existente = pagoRepository.findById(id);
            if (existente == null) {
                return notFound(id);
            }

            // La factura de un pago no se cambia
            if (request.idFactura != null
                    && (existente.getIdFactura() == null
                    || !request.idFactura.equals(existente.getIdFactura().getIdFactura()))) {
                return conflicto("No se puede cambiar la factura de un pago existente; elimínelo y cree otro.");
            }

            // Validar todo ANTES de modificar la entidad
            String estado = request.estado != null ? request.estado.trim() : null;
            if (estado != null && estado.length() > ESTADO_MAX) {
                return badRequest("El estado no puede superar " + ESTADO_MAX + " caracteres");
            }

            if (estado != null) {
                existente.setEstado(estado);
            }
            if (request.observaciones != null) {
                existente.setObservaciones(request.observaciones);
            }

            pagoRepository.modificar(existente);
            return Response.ok(existente).build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return serverError(ex);
        }
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        if (id == null) {
            return badRequest("El ID no puede ser nulo");
        }
        try {
            Pago encontrado = pagoRepository.findById(id);
            if (encontrado == null) {
                return notFound(id);
            }

            // Revisar lo que impide borrar ANTES de borrar
            long detalles = pagoRepository.countDetalles(id);
            if (detalles > 0) {
                return conflicto("No se pudo eliminar el pago porque está asociado a "
                        + detalles + " detalle(s) de pago. Elimine primero sus detalles.");
            }

            pagoRepository.eliminar(encontrado);
            return Response.noContent().build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar pago: " + id, ex);
            return serverError(ex);
        }
    }
}