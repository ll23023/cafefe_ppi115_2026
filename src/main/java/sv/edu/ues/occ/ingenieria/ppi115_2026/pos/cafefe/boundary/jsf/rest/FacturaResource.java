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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CajaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caja;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;

import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.badRequest;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.conflicto;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.notFound;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.serverError;

/**
 *
 * @author mil
 */
@Path("factura")
public class FacturaResource {

    private static final Logger LOG = Logger.getLogger(FacturaResource.class.getName());
    private static final int CLIENTE_MAX = 155;
    private static final int ESTADO_MAX = 20;

    @Inject
    FacturaRepository facturaRepository;

    @Inject
    EmpleadoRolRepository empleadoRolRepository;

    @Inject
    CajaRepository cajaRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<Factura> facturas = facturaRepository.findRange(first, max);
                Long total = facturaRepository.count();
                return Response.ok(facturas)
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
            Factura encontrada = facturaRepository.findById(id);
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
    public Response create(FacturaRequest request, @Context UriInfo uriInfo) {
        if (request == null || request.idEmpleadoRol == null) {
            return badRequest("idEmpleadoRol es obligatorio");
        }
        if (request.idCaja == null) {
            return badRequest("idCaja es obligatorio");
        }
        String cliente = request.cliente != null ? request.cliente.trim() : "";
        String estado = request.estado != null ? request.estado.trim() : null;
        Response invalido = validarTextos(cliente, estado);
        if (invalido != null) {
            return invalido;
        }

        try {
            EmpleadoRol empleadoRol = empleadoRolRepository.findById(request.idEmpleadoRol);
            if (empleadoRol == null) {
                return conflicto("La asignación de rol no existe: " + request.idEmpleadoRol);
            }
            if (!Boolean.TRUE.equals(empleadoRol.getActivo())) {
                return conflicto("No se puede facturar con una asignación de rol inactiva.");
            }

            Caja caja = cajaRepository.findById(request.idCaja);
            if (caja == null) {
                return conflicto("La caja no existe: " + request.idCaja);
            }

            Factura nueva = new Factura(UUID.randomUUID());
            nueva.setIdEmpleadoRol(empleadoRol);
            nueva.setIdCaja(caja);
            nueva.setCliente(cliente);
            nueva.setEstado(estado);
            nueva.setObservaciones(request.observaciones != null ? request.observaciones : "");
            nueva.setFechaFacturacion(new Date());
            facturaRepository.crear(nueva);

            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(nueva.getIdFactura()))
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
    public Response update(@PathParam("id") UUID id, FacturaRequest request) {
        if (id == null || request == null) {
            return badRequest("ID o cuerpo nulo");
        }
        try {
            Factura existente = facturaRepository.findById(id);
            if (existente == null) {
                return notFound(id);
            }

            // Quién emitió la factura y en qué caja no se cambia
            if (request.idEmpleadoRol != null
                    && (existente.getIdEmpleadoRol() == null
                    || !request.idEmpleadoRol.equals(existente.getIdEmpleadoRol().getIdEmpleadoRol()))) {
                return conflicto("No se puede cambiar la asignación de rol de una factura existente.");
            }
            if (request.idCaja != null
                    && (existente.getIdCaja() == null
                    || !request.idCaja.equals(existente.getIdCaja().getIdCaja()))) {
                return conflicto("No se puede cambiar la caja de una factura existente.");
            }

            // Validar todo ANTES de modificar la entidad
            String cliente = request.cliente != null ? request.cliente.trim() : null;
            String estado = request.estado != null ? request.estado.trim() : null;
            Response invalido = validarTextos(cliente, estado);
            if (invalido != null) {
                return invalido;
            }

            if (cliente != null) {
                existente.setCliente(cliente);
            }
            if (estado != null) {
                existente.setEstado(estado);
            }
            if (request.observaciones != null) {
                existente.setObservaciones(request.observaciones);
            }

            facturaRepository.modificar(existente);
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
            Factura encontrada = facturaRepository.findById(id);
            if (encontrada == null) {
                return notFound(id);
            }

            // Revisar lo que impide borrar ANTES de borrar
            long pagos = facturaRepository.countPagos(id);
            long lineas = facturaRepository.countLineas(id);

            List<String> partes = new ArrayList<>();
            if (pagos > 0) {
                partes.add(pagos + " pago(s)");
            }
            if (lineas > 0) {
                partes.add(lineas + " línea(s) de orden");
            }
            if (!partes.isEmpty()) {
                return conflicto("No se pudo eliminar la factura" + describirCliente(encontrada)
                        + " porque está asociada a " + String.join(" y a ", partes)
                        + ". Elimine primero esos registros.");
            }

            facturaRepository.eliminar(encontrada);
            return Response.noContent().build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar factura: " + id, ex);
            return serverError(ex);
        }
    }

    private String describirCliente(Factura f) {
        if (f.getCliente() == null || f.getCliente().isBlank()) {
            return "";
        }
        return " del cliente '" + f.getCliente() + "'";
    }

    /**
     * @return null si los textos son válidos, o la respuesta 400 si no lo son
     */
    private Response validarTextos(String cliente, String estado) {
        if (cliente != null && cliente.length() > CLIENTE_MAX) {
            return badRequest("El cliente no puede superar " + CLIENTE_MAX + " caracteres");
        }
        if (estado != null && estado.length() > ESTADO_MAX) {
            return badRequest("El estado no puede superar " + ESTADO_MAX + " caracteres");
        }
        return null;
    }
}