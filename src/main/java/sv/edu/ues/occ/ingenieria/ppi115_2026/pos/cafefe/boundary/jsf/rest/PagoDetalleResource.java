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
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoDetalleRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Pago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.PagoDetalle;

import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.badRequest;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.conflicto;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.notFound;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.serverError;

/**
 *
 * @author mil
 */
@Path("pago-detalle")
public class PagoDetalleResource {

    private static final Logger LOG = Logger.getLogger(PagoDetalleResource.class.getName());
    private static final int TIPO_PAGO_MAX = 20;
    private static final BigDecimal MONTO_MAX = new BigDecimal("999999.99");

    @Inject
    PagoDetalleRepository pagoDetalleRepository;

    @Inject
    PagoRepository pagoRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max,
            @QueryParam("idPago") UUID idPago) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<PagoDetalle> detalles;
                Long total;
                if (idPago != null) {
                    detalles = pagoDetalleRepository.findByPago(idPago, first, max);
                    total = pagoDetalleRepository.countByPago(idPago);
                } else {
                    detalles = pagoDetalleRepository.findRange(first, max);
                    total = pagoDetalleRepository.count();
                }
                return Response.ok(detalles)
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
            PagoDetalle encontrado = pagoDetalleRepository.findById(id);
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
    public Response create(PagoDetalleRequest request, @Context UriInfo uriInfo) {
        if (request == null || request.idPago == null) {
            return badRequest("idPago es obligatorio");
        }
        if (request.monto == null) {
            return badRequest("El monto es obligatorio");
        }
        Response montoInvalido = validarMonto(request.monto);
        if (montoInvalido != null) {
            return montoInvalido;
        }
        if (request.tipoPago == null || request.tipoPago.trim().isEmpty()) {
            return badRequest("El tipoPago es obligatorio");
        }
        String tipoPago = request.tipoPago.trim();
        if (tipoPago.length() > TIPO_PAGO_MAX) {
            return badRequest("El tipoPago no puede superar " + TIPO_PAGO_MAX + " caracteres");
        }

        try {
            Pago pago = pagoRepository.findById(request.idPago);
            if (pago == null) {
                return conflicto("El pago no existe: " + request.idPago);
            }

            PagoDetalle nuevo = new PagoDetalle(UUID.randomUUID());
            nuevo.setIdPago(pago);
            nuevo.setMonto(request.monto);
            nuevo.setTipoPago(tipoPago);
            nuevo.setObservaciones(request.observaciones != null ? request.observaciones : "");
            nuevo.setReferenciaExterna(request.referenciaExterna != null ? request.referenciaExterna : "");
            pagoDetalleRepository.crear(nuevo);

            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(nuevo.getIdPagoDetalle()))
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
    public Response update(@PathParam("id") UUID id, PagoDetalleRequest request) {
        if (id == null || request == null) {
            return badRequest("ID o cuerpo nulo");
        }
        try {
            PagoDetalle existente = pagoDetalleRepository.findById(id);
            if (existente == null) {
                return notFound(id);
            }

            // El pago de un detalle no se cambia
            if (request.idPago != null
                    && (existente.getIdPago() == null
                    || !request.idPago.equals(existente.getIdPago().getIdPago()))) {
                return conflicto("No se puede cambiar el pago de un detalle existente; elimínelo y cree otro.");
            }

            // Validar todo ANTES de modificar la entidad
            if (request.monto != null) {
                Response montoInvalido = validarMonto(request.monto);
                if (montoInvalido != null) {
                    return montoInvalido;
                }
            }
            String tipoPago = null;
            if (request.tipoPago != null) {
                tipoPago = request.tipoPago.trim();
                if (tipoPago.isEmpty()) {
                    return badRequest("El tipoPago no puede estar vacío");
                }
                if (tipoPago.length() > TIPO_PAGO_MAX) {
                    return badRequest("El tipoPago no puede superar " + TIPO_PAGO_MAX + " caracteres");
                }
            }

            if (request.monto != null) {
                existente.setMonto(request.monto);
            }
            if (tipoPago != null) {
                existente.setTipoPago(tipoPago);
            }
            if (request.observaciones != null) {
                existente.setObservaciones(request.observaciones);
            }
            if (request.referenciaExterna != null) {
                existente.setReferenciaExterna(request.referenciaExterna);
            }

            pagoDetalleRepository.modificar(existente);
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
            PagoDetalle encontrado = pagoDetalleRepository.findById(id);
            if (encontrado == null) {
                return notFound(id);
            }
            // Ninguna tabla referencia a pago_detalle, se puede borrar directo
            pagoDetalleRepository.eliminar(encontrado);
            return Response.noContent().build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar pago_detalle: " + id, ex);
            return serverError(ex);
        }
    }

    /**
     * @return null si el monto es válido, o la respuesta 400 si no lo es
     */
    private Response validarMonto(BigDecimal monto) {
        if (monto.signum() <= 0) {
            return badRequest("El monto debe ser mayor que 0");
        }
        if (monto.stripTrailingZeros().scale() > 2) {
            return badRequest("El monto admite máximo 2 decimales");
        }
        if (monto.compareTo(MONTO_MAX) > 0) {
            return badRequest("El monto no puede superar " + MONTO_MAX);
        }
        return null;
    }
}