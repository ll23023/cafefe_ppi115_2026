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

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaOrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.FacturaOrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.OrdenProducto;

import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.badRequest;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.conflicto;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.notFound;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.serverError;

/**
 *
 * @author mil
 */
@Path("factura-orden-producto")
public class FacturaOrdenProductoResource {

    private static final Logger LOG = Logger.getLogger(FacturaOrdenProductoResource.class.getName());
    private static final BigDecimal PRECIO_MAX = new BigDecimal("999999.99");

    @Inject
    FacturaOrdenProductoRepository facturaOrdenProductoRepository;

    @Inject
    FacturaRepository facturaRepository;

    @Inject
    OrdenProductoRepository ordenProductoRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max,
            @QueryParam("idFactura") UUID idFactura) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<FacturaOrdenProducto> lineas;
                Long total;
                if (idFactura != null) {
                    lineas = facturaOrdenProductoRepository.findByFactura(idFactura, first, max);
                    total = facturaOrdenProductoRepository.countByFactura(idFactura);
                } else {
                    lineas = facturaOrdenProductoRepository.findRange(first, max);
                    total = facturaOrdenProductoRepository.count();
                }
                return Response.ok(lineas)
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
            FacturaOrdenProducto encontrada = facturaOrdenProductoRepository.findById(id);
            if (encontrada != null) {
                return Response.ok(encontrada).build();
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
    public Response create(FacturaOrdenProductoRequest request, @Context UriInfo uriInfo) {
        if (request == null || request.idFactura == null) {
            return badRequest("idFactura es obligatorio");
        }
        if (request.idOrdenProducto == null) {
            return badRequest("idOrdenProducto es obligatorio");
        }
        if (request.precio == null) {
            return badRequest("El precio es obligatorio");
        }
        Response precioInvalido = validarPrecio(request.precio);
        if (precioInvalido != null) {
            return precioInvalido;
        }

        try {
            Factura factura = facturaRepository.findById(request.idFactura);
            if (factura == null) {
                return conflicto("La factura no existe: " + request.idFactura);
            }
            OrdenProducto ordenProducto = ordenProductoRepository.findById(request.idOrdenProducto);
            if (ordenProducto == null) {
                return conflicto("El producto de orden no existe: " + request.idOrdenProducto);
            }
            if (facturaOrdenProductoRepository.countByFacturaAndOrdenProducto(
                    request.idFactura, request.idOrdenProducto) > 0) {
                return conflicto("Ese producto de orden ya está incluido en la factura.");
            }

            FacturaOrdenProducto nueva = new FacturaOrdenProducto(UUID.randomUUID());
            nueva.setIdFactura(factura);
            nueva.setIdOrdenProducto(ordenProducto);
            nueva.setPrecio(request.precio);
            nueva.setObservaciones(request.observaciones != null ? request.observaciones : "");
            facturaOrdenProductoRepository.crear(nueva);

            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(nueva.getIdFacturaOrdenProducto()))
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
    public Response update(@PathParam("id") UUID id, FacturaOrdenProductoRequest request) {
        if (id == null || request == null) {
            return badRequest("ID o cuerpo nulo");
        }
        try {
            FacturaOrdenProducto existente = facturaOrdenProductoRepository.findById(id);
            if (existente == null) {
                return notFound(id);
            }

            // La factura y el producto de orden de una línea no se cambian
            if (request.idFactura != null
                    && (existente.getIdFactura() == null
                    || !request.idFactura.equals(existente.getIdFactura().getIdFactura()))) {
                return conflicto("No se puede cambiar la factura de una línea existente; elimínela y cree otra.");
            }
            if (request.idOrdenProducto != null
                    && (existente.getIdOrdenProducto() == null
                    || !request.idOrdenProducto.equals(existente.getIdOrdenProducto().getIdOrdenProducto()))) {
                return conflicto("No se puede cambiar el producto de orden de una línea existente; elimínela y cree otra.");
            }

            // Validar todo ANTES de modificar la entidad
            if (request.precio != null) {
                Response precioInvalido = validarPrecio(request.precio);
                if (precioInvalido != null) {
                    return precioInvalido;
                }
                existente.setPrecio(request.precio);
            }
            if (request.observaciones != null) {
                existente.setObservaciones(request.observaciones);
            }

            facturaOrdenProductoRepository.modificar(existente);
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
            FacturaOrdenProducto encontrada = facturaOrdenProductoRepository.findById(id);
            if (encontrada == null) {
                return notFound(id);
            }
            // Ninguna tabla referencia a factura_orden_producto, se puede borrar directo
            facturaOrdenProductoRepository.eliminar(encontrada);
            return Response.noContent().build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar factura_orden_producto: " + id, ex);
            return serverError(ex);
        }
    }

    /**
     * @return null si el precio es válido, o la respuesta 400 si no lo es
     */
    private Response validarPrecio(BigDecimal precio) {
        if (precio.signum() < 0) {
            return badRequest("El precio no puede ser negativo");
        }
        if (precio.stripTrailingZeros().scale() > 2) {
            return badRequest("El precio admite máximo 2 decimales");
        }
        if (precio.compareTo(PRECIO_MAX) > 0) {
            return badRequest("El precio no puede superar " + PRECIO_MAX);
        }
        return null;
    }
}