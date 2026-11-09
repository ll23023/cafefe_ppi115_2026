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
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest.ResponseHeaders;
import jakarta.persistence.PersistenceException;

/**
 * 
 * @author mil
 */
@Path("empleado")
public class EmpleadoResource implements Serializable {

    private static final Logger LOG = Logger.getLogger(EmpleadoResource.class.getName());

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    EmpleadoRolRepository empleadoRolRepository;

    @Inject
    RolRepository rolRepository;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<Empleado> empleados = empleadoRepository.findRange(first, max);
                Long total = empleadoRepository.count();

                return Response.ok(empleados)
                        .header(ResponseHeaders.TOTAL_REGISTROS.toString(), total)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .header(ResponseHeaders.WRONG_PARAMETER.toString(),
                                "Los parámetros 'first' debe ser >= 0 y 'max' debe ser > 0 y <= 50")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                    .build();
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Empleado encontrado = empleadoRepository.findById(id);
                if (encontrado != null) {
                    return Response.ok(encontrado).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header(ResponseHeaders.NOT_FOUND.toString(), "id: " + id)
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                        .build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .header(ResponseHeaders.WRONG_PARAMETER.toString(), "id: " + id)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional // Asegura que el empleado y sus roles se guarden juntos o no se guarde nada
    public Response create(EmpleadoRequest request, @Context UriInfo uriInfo) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header(ResponseHeaders.WRONG_PARAMETER.toString(), "El cuerpo de la petición no puede ser nulo")
                    .build();
        }

        try {
            // Validaciones básicas
            if (request.nombre == null || request.nombre.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .header(ResponseHeaders.WRONG_PARAMETER.toString(), "El nombre es obligatorio")
                        .build();
            }

            // Validaciones con respecto a duplicados
            String nombreTrim = request.nombre.trim();
            String apellidoTrim = request.apellido != null ? request.apellido.trim() : "";

            boolean repetido = empleadoRepository.findAll().stream()
                    .anyMatch(e -> e.getNombre() != null && e.getApellido() != null
                    && e.getNombre().equalsIgnoreCase(nombreTrim)
                    && e.getApellido().equalsIgnoreCase(apellidoTrim));

            if (repetido) {
                return Response.status(Response.Status.CONFLICT)
                        .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(),
                                "Ya existe un empleado con ese nombre y apellido.")
                        .build();
            }

            Empleado nuevo = new Empleado(UUID.randomUUID());
            nuevo.setNombre(nombreTrim);
            nuevo.setApellido(apellidoTrim);
            nuevo.setActivo(request.activo != null ? request.activo : Boolean.TRUE);
            nuevo.setComentarios(request.comentarios != null ? request.comentarios : "");

            // Guarda el empleado
            empleadoRepository.crear(nuevo);
            Empleado empGuardado = empleadoRepository.findById(nuevo.getIdEmpleado());

            // Asigna los roles
            if (request.roles != null && !request.roles.isEmpty()) {
                asignarRoles(empGuardado, request.roles, true);
            }

            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(nuevo.getIdEmpleado()))
                    .build();
            return Response.created(location).build(); // 201

        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.CONFLICT) // 409
                    .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), ex.getMessage())
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@PathParam("id") UUID id, EmpleadoRequest request) {
        if (id == null || request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header(ResponseHeaders.WRONG_PARAMETER.toString(), "ID o cuerpo nulo")
                    .build();
        }

        try {
            Empleado existente = empleadoRepository.findById(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header(ResponseHeaders.NOT_FOUND.toString(), "Recurso no encontrado")
                        .build();
            }

            if (request.nombre == null || request.nombre.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .header(ResponseHeaders.WRONG_PARAMETER.toString(), "El nombre es obligatorio")
                        .build();
            }

            //Si al modificar ya tiene un rol, no se permite agregar más
            List<EmpleadoRol> asignacionesActuales = obtenerAsignaciones(existente);
            if (!asignacionesActuales.isEmpty() && request.roles != null && !request.roles.isEmpty()) {
                return Response.status(Response.Status.CONFLICT)
                        .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(),
                                "El empleado ya tiene un rol asignado; no se pueden agregar más.")
                        .build();
            }

            existente.setNombre(request.nombre.trim());
            existente.setApellido(request.apellido != null ? request.apellido.trim() : "");
            existente.setActivo(request.activo != null ? request.activo : existente.getActivo());
            existente.setComentarios(request.comentarios != null ? request.comentarios : "");

            empleadoRepository.modificar(existente);

            // Si se enviaron roles nuevos y no tenía, asignarlos
            if (request.roles != null && !request.roles.isEmpty()) {
                asignarRoles(existente, request.roles, true);
            }

            return Response.ok(existente).build();

        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), ex.getMessage())
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header(ResponseHeaders.WRONG_PARAMETER.toString(), "El ID no puede ser nulo")
                    .build();
        }

        try {
            Empleado encontrado = empleadoRepository.findById(id);
            if (encontrado == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header(ResponseHeaders.NOT_FOUND.toString(), "Recurso no encontrado")
                        .build();
            }

            // Eliminar roles
            for (EmpleadoRol er : obtenerAsignaciones(encontrado)) {
                empleadoRolRepository.eliminar(er);
            }

            // Eliminar al empleado
            empleadoRepository.eliminar(encontrado);

            return Response.noContent().build(); // 204

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar empleado", ex);

            // Buscamos si el error es porque tiene facturas/órdenes asociadas
            boolean esErrorDeIntegridad = false;
            Throwable causa = ex;
            while (causa != null) {
                if (causa instanceof jakarta.persistence.PersistenceException) {
                    esErrorDeIntegridad = true;
                    break;
                }
                causa = causa.getCause();
            }

            if (esErrorDeIntegridad) {
                return Response.status(Response.Status.CONFLICT) // 409 Conflict
                        .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(),
                                "No se puede eliminar el empleado porque tiene registros asociados (facturas, órdenes, etc.).")
                        .build();
            }

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                    .build();
        }
    }

    private List<EmpleadoRol> obtenerAsignaciones(Empleado e) {
        if (e == null || e.getIdEmpleado() == null) {
            return new ArrayList<>();
        }
        return empleadoRolRepository.findAll().stream()
                .filter(er -> er.getIdEmpleado() != null
                && e.getIdEmpleado().equals(er.getIdEmpleado().getIdEmpleado()))
                .toList();
    }

    private void asignarRoles(Empleado emp, List<String> nombresRoles, boolean crearSiNoExisten) {
        Map<String, Rol> rolesPorNombre = new HashMap<>();
        for (Rol r : rolRepository.findAll()) {
            if (r.getNombre() != null) {
                rolesPorNombre.putIfAbsent(r.getNombre().trim().toLowerCase(Locale.ROOT), r);
            }
        }

        Set<String> yaAsignados = new HashSet<>();
        for (EmpleadoRol er : obtenerAsignaciones(emp)) {
            if (er.getIdRol() != null && er.getIdRol().getNombre() != null) {
                yaAsignados.add(er.getIdRol().getNombre().trim().toLowerCase(Locale.ROOT));
            }
        }

        for (String nombreRol : nombresRoles) {
            String clave = nombreRol.trim().toLowerCase(Locale.ROOT);
            if (clave.isEmpty()) {
                continue;
            }

            if (yaAsignados.contains(clave)) {
                throw new IllegalArgumentException("El empleado ya tiene asignado el rol: " + nombreRol);
            }

            Rol rol = rolesPorNombre.get(clave);
            if (rol == null) {
                if (!crearSiNoExisten) {
                    throw new IllegalArgumentException("El rol no existe: " + nombreRol);
                }
                rol = new Rol(UUID.randomUUID());
                rol.setNombre(nombreRol.trim());
                rol.setActivo(Boolean.TRUE);
                rol.setObservaciones("");
                rolRepository.crear(rol);
                rolesPorNombre.put(clave, rol);
            } else if (!Boolean.TRUE.equals(rol.getActivo())) {
                throw new IllegalArgumentException("No se puede asignar un rol inactivo: " + rol.getNombre());
            }

            EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
            er.setIdEmpleado(emp);
            er.setIdRol(rol);
            er.setActivo(Boolean.TRUE);
            er.setObservaciones("");
            empleadoRolRepository.crear(er);
        }
    }

}
