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
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 *
 * @author mil
 */
@Path("empleado-rol")
public class EmpleadoRolResource {

    private static final Logger LOG = Logger.getLogger(EmpleadoRolResource.class.getName());

    @Inject
    EmpleadoRolRepository empleadoRolRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    RolRepository rolRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<EmpleadoRol> asignaciones = empleadoRolRepository.findRange(first, max);
                Long total = empleadoRolRepository.count();
                return Response.ok(asignaciones)
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
            EmpleadoRol encontrado = empleadoRolRepository.findById(id);
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
    public Response create(EmpleadoRolRequest request, @Context UriInfo uriInfo) {
        if (request == null || request.idEmpleado == null || request.idRol == null) {
            return badRequest("idEmpleado e idRol son obligatorios");
        }
        try {
            // Todas las validaciones van ANTES de escribir
            Empleado empleado = empleadoRepository.findById(request.idEmpleado);
            if (empleado == null) {
                return conflicto("El empleado no existe: " + request.idEmpleado);
            }

            Rol rol = rolRepository.findById(request.idRol);
            if (rol == null) {
                return conflicto("El rol no existe: " + request.idRol);
            }
            if (!Boolean.TRUE.equals(rol.getActivo())) {
                return conflicto("No se puede asignar un rol inactivo: " + rol.getNombre());
            }

            // un empleado solo puede tener un rol
            if (!empleadoRolRepository.findByEmpleado(request.idEmpleado).isEmpty()) {
                return conflicto("El empleado ya tiene un rol asignado; no se pueden agregar más.");
            }

            EmpleadoRol nuevo = new EmpleadoRol(UUID.randomUUID());
            nuevo.setIdEmpleado(empleado);
            nuevo.setIdRol(rol);
            nuevo.setActivo(request.activo != null ? request.activo : Boolean.TRUE);
            nuevo.setObservaciones(request.observaciones != null ? request.observaciones : "");
            empleadoRolRepository.crear(nuevo);

            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(nuevo.getIdEmpleadoRol()))
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
    public Response update(@PathParam("id") UUID id, EmpleadoRolRequest request) {
        if (id == null || request == null) {
            return badRequest("ID o cuerpo nulo");
        }
        try {
            EmpleadoRol existente = empleadoRolRepository.findById(id);
            if (existente == null) {
                return notFound(id);
            }

            // El empleado de una asignación no se cambia: facturas y órdenes ya la referencian
            if (request.idEmpleado != null
                    && (existente.getIdEmpleado() == null
                    || !request.idEmpleado.equals(existente.getIdEmpleado().getIdEmpleado()))) {
                return conflicto("No se puede cambiar el empleado de una asignación existente; elimínela y cree otra.");
            }

            // Cambio de rol (opcional)
            if (request.idRol != null
                    && (existente.getIdRol() == null
                    || !request.idRol.equals(existente.getIdRol().getIdRol()))) {
                Rol rol = rolRepository.findById(request.idRol);
                if (rol == null) {
                    return conflicto("El rol no existe: " + request.idRol);
                }
                if (!Boolean.TRUE.equals(rol.getActivo())) {
                    return conflicto("No se puede asignar un rol inactivo: " + rol.getNombre());
                }
                existente.setIdRol(rol);
            }

            existente.setActivo(request.activo != null ? request.activo : existente.getActivo());
            existente.setObservaciones(request.observaciones != null ? request.observaciones : "");

            empleadoRolRepository.modificar(existente);
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
            EmpleadoRol encontrado = empleadoRolRepository.findById(id);
            if (encontrado == null) {
                return notFound(id);
            }

            // Validacion: Revisar lo que impide borrar antes de borrar
            long facturas = empleadoRolRepository.countFacturas(id);
            long ordenes = empleadoRolRepository.countOrdenes(id);

            List<String> partes = new ArrayList<>();
            if (facturas > 0) {
                partes.add(facturas + " factura(s)");
            }
            if (ordenes > 0) {
                partes.add(ordenes + " orden(es)");
            }
            if (!partes.isEmpty()) {
                return conflicto("No se pudo eliminar " + describir(encontrado)
                        + " porque está asociada a " + String.join(" y a ", partes) + ".");
            }

            empleadoRolRepository.eliminar(encontrado);
            return Response.noContent().build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar empleado_rol: " + id, ex);
            return serverError(ex);
        }
    }

    private String describir(EmpleadoRol er) {
        String rol = er.getIdRol() != null && er.getIdRol().getNombre() != null
                ? er.getIdRol().getNombre() : "?";
        String empleado = "?";
        if (er.getIdEmpleado() != null) {
            String nombre = er.getIdEmpleado().getNombre() != null ? er.getIdEmpleado().getNombre() : "";
            String apellido = er.getIdEmpleado().getApellido() != null ? er.getIdEmpleado().getApellido() : "";
            empleado = (nombre + " " + apellido).trim();
        }
        return "la asignación del rol '" + rol + "' al empleado '" + empleado + "'";
    }

    /**
     * 409: el mensaje completo va en el body; en el header va sin tildes porque
     * los headers HTTP deben ser ASCII.
     */
    private Response conflicto(String mensaje) {
        return Response.status(Response.Status.CONFLICT)
                .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), ascii(mensaje))
                .entity(Map.of("error", mensaje))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response badRequest(String mensaje) {
        return Response.status(Response.Status.BAD_REQUEST)
                .header(ResponseHeaders.WRONG_PARAMETER.toString(), ascii(mensaje))
                .entity(Map.of("error", mensaje))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response notFound(UUID id) {
        return Response.status(Response.Status.NOT_FOUND)
                .header(ResponseHeaders.NOT_FOUND.toString(), "id: " + id)
                .build();
    }

    private Response serverError(Exception ex) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .header(ResponseHeaders.PROCESS_ERROR.toString(), ascii(String.valueOf(ex.getMessage())))
                .build();
    }

    private static String ascii(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}