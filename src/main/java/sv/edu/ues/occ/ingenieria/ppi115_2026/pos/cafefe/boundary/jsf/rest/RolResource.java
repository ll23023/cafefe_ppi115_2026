package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import jakarta.inject.Inject;
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
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

@Path("rol")
public class RolResource implements Serializable {

    private static final Logger LOG = Logger.getLogger(RolResource.class.getName());

    @Inject
    RolRepository rolRepository;

    @Inject
    EmpleadoRolRepository empleadoRolRepository;

    /**
     * GET /rol?first=0&max=50
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<Rol> roles = rolRepository.findRange(first, max);
                Long total = rolRepository.count();
                
                Response.ResponseBuilder responseBuilder = Response.ok(roles)
                        .header(ResponseHeaders.TOTAL_REGISTROS.toString(), total)
                        .type(MediaType.APPLICATION_JSON);
                return responseBuilder.build();
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

    /**
     * GET /rol/{id}
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Rol encontrado = rolRepository.findById(id);
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

    /**
     * POST /rol
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Rol nuevo, @Context UriInfo uriInfo) {
        if (nuevo != null && nuevo.getIdRol() == null) {
            try {
                String nombre = nuevo.getNombre() == null ? "" : nuevo.getNombre().trim();
                nuevo.setNombre(nombre);
                
                // Validación: El nombre no se puede repetir
                boolean repetido = rolRepository.findAll().stream()
                        .anyMatch(o -> o.getNombre() != null
                        && o.getNombre().trim().equalsIgnoreCase(nombre));
                
                if (repetido) {
                    return Response.status(Response.Status.CONFLICT) // 409 Conflict
                            .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), 
                                    "Ya existe un rol llamado \"" + nombre + "\".")
                            .build();
                }

                nuevo.setIdRol(UUID.randomUUID());
                rolRepository.crear(nuevo);
                
                URI location = uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(nuevo.getIdRol()))
                        .build();
                return Response.created(location).build(); // 201 Created
                
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                        .build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .header(ResponseHeaders.WRONG_PARAMETER.toString(), "El recurso no puede ser nulo y no debe tener un ID asignado")
                .build();
    }

    /**
     * PUT /rol/{id}
     */
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Rol nuevo) {
        if (nuevo != null && id != null) {
            try {
                Rol existente = rolRepository.findById(id);
                if (existente != null) {
                    
                    String nombre = nuevo.getNombre() == null ? "" : nuevo.getNombre().trim();
                    nuevo.setNombre(nombre);
                    
                    // Validación: El nombre no se puede repetir (excluyendo el mismo rol)
                    boolean repetido = rolRepository.findAll().stream()
                            .anyMatch(o -> o.getNombre() != null
                            && o.getNombre().trim().equalsIgnoreCase(nombre)
                            && !o.getIdRol().equals(id)); 
                    
                    if (repetido) {
                        return Response.status(Response.Status.CONFLICT) // 409 Conflict
                                .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), 
                                        "Ya existe otro rol llamado \"" + nombre + "\".")
                                .build();
                    }
                    
                    nuevo.setIdRol(id);
                    rolRepository.modificar(nuevo);
                    return Response.ok(nuevo).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header(ResponseHeaders.NOT_FOUND.toString(), "Recurso no encontrado")
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                        .build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .header(ResponseHeaders.WRONG_PARAMETER.toString(), "El recurso no puede ser nulo y debe tener un ID asignado")
                .build();
    }

    /**
     * DELETE /rol/{id}
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Rol encontrado = rolRepository.findById(id);
                if (encontrado != null) {
                    
                    // Validación: No se puede borrar un rol con empleados asignados
                    long asignados = empleadoRolRepository.findAll().stream()
                            .filter(er -> er.getIdRol() != null && er.getIdRol().getIdRol().equals(id))
                            .count();
                    
                    if (asignados > 0) {
                        return Response.status(Response.Status.CONFLICT) // 409 Conflict
                                .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), 
                                        "No se puede eliminar \"" + encontrado.getNombre() + "\": lo tienen " + asignados + " empleado(s). Puede marcarlo como inactivo en su lugar.")
                                .build();
                    }
                    
                    rolRepository.eliminar(encontrado);
                    return Response.noContent().build(); // 204 No Content
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header(ResponseHeaders.NOT_FOUND.toString(), "Recurso no encontrado")
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage())
                        .build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .header(ResponseHeaders.WRONG_PARAMETER.toString(), "El ID no puede ser nulo")
                .build();
    }
}