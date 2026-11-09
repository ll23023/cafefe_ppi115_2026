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

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Orden;

import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.badRequest;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.conflicto;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.notFound;
import static sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.RestResponses.serverError;

/**
 *
 * @author mil
 */
@Path("orden")
public class OrdenResource {

    private static final Logger LOG = Logger.getLogger(OrdenResource.class.getName());

    @Inject
    OrdenRepository ordenRepository;

    @Inject
    EmpleadoRolRepository empleadoRolRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<Orden> ordenes = ordenRepository.findRange(first, max);
                Long total = ordenRepository.count();
                return Response.ok(ordenes)
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
            Orden encontrada = ordenRepository.findById(id);
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
    public Response create(OrdenRequest request, @Context UriInfo uriInfo) {
        if (request == null || request.idEmpleadoRol == null) {
            return badRequest("idEmpleadoRol es obligatorio");
        }
        try {
            EmpleadoRol empleadoRol = empleadoRolRepository.findById(request.idEmpleadoRol);
            if (empleadoRol == null) {
                return conflicto("La asignación de rol no existe: " + request.idEmpleadoRol);
            }
            if (!Boolean.TRUE.equals(empleadoRol.getActivo())) {
                return conflicto("No se puede usar una asignación de rol inactiva.");
            }

            Orden nueva = new Orden(UUID.randomUUID());
            nueva.setIdEmpleadoRol(empleadoRol);
            nueva.setFechaCreacion(new Date());
            ordenRepository.crear(nueva);

            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(nueva.getIdOrden()))
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
    public Response update(@PathParam("id") UUID id, OrdenRequest request) {
        if (id == null || request == null) {
            return badRequest("ID o cuerpo nulo");
        }
        try {
            Orden existente = ordenRepository.findById(id);
            if (existente == null) {
                return notFound(id);
            }

            // Reasignar la orden a otra asignación de rol (opcional)
            if (request.idEmpleadoRol != null
                    && (existente.getIdEmpleadoRol() == null
                    || !request.idEmpleadoRol.equals(existente.getIdEmpleadoRol().getIdEmpleadoRol()))) {
                EmpleadoRol empleadoRol = empleadoRolRepository.findById(request.idEmpleadoRol);
                if (empleadoRol == null) {
                    return conflicto("La asignación de rol no existe: " + request.idEmpleadoRol);
                }
                if (!Boolean.TRUE.equals(empleadoRol.getActivo())) {
                    return conflicto("No se puede usar una asignación de rol inactiva.");
                }
                existente.setIdEmpleadoRol(empleadoRol);
            }

            ordenRepository.modificar(existente);
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
            Orden encontrada = ordenRepository.findById(id);
            if (encontrada == null) {
                return notFound(id);
            }

            // Revisar lo que impide borrar ANTES de borrar
            long productos = ordenRepository.countOrdenProductos(id);
            long facturadas = ordenRepository.countLineasFacturadas(id);

            List<String> partes = new ArrayList<>();
            if (productos > 0) {
                partes.add(productos + " producto(s) de orden");
            }
            if (facturadas > 0) {
                partes.add(facturadas + " línea(s) de factura");
            }
            if (!partes.isEmpty()) {
                return conflicto("No se pudo eliminar la orden porque está asociada a "
                        + String.join(" y a ", partes) + ". Elimine primero esos registros.");
            }

            ordenRepository.eliminar(encontrada);
            return Response.noContent().build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar orden: " + id, ex);
            return serverError(ex);
        }
    }
}