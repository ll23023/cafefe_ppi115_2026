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
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;
import java.util.UUID;

import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento;

@Path("tipo_descuento")
public class TipoDescuentoResource implements Serializable {

    @Inject
    TipoDescuentoRepository tdRepo;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("desde")
            @DefaultValue("1") int desde,
            @QueryParam("hasta")
            @DefaultValue("50") int hasta) {

        if (0 < desde && desde < hasta) {
            try {
                return Response.ok(tdRepo.findRange(desde, hasta))
                        .type(MediaType.APPLICATION_JSON)
                        .build();

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);

                return Response.status(500)
                        .header("App-Error", e.getMessage())
                        .build();
            }
        }
        
        return Response.status(422)
                .header("Wrong-Param", "cantidad")
                .build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") UUID id) {

        if (id != null) {
            try {
                TipoDescuento encontrado = tdRepo.findById(id);

                if (encontrado != null) {
                    return Response.ok(encontrado).type(MediaType.APPLICATION_JSON)
                            .build();

                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .header("Entity-Not-Found", id)
                            .build();
                }

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);

                return Response.status(500)
                        .header("App-Error", e.getMessage())
                        .build();
            }
        }

        return Response.status(422)
                .header("Wrong-Param", "id")
                .build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response nuevo(
            TipoDescuento td,
            @Context UriInfo uInf) {

        if (td != null) {
            td.setIdTipoDescuento(UUID.randomUUID());
            td.setDescuentoList(null);

            try {
                tdRepo.crear(td);

                UriBuilder uBldr = uInf.getAbsolutePathBuilder();
                uBldr.path(td.getIdTipoDescuento().toString());

                return Response.created(uBldr.build()).build();

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);

                return Response.status(500)
                        .header("App-Error", e.getMessage())
                        .build();
            }
        }

        return Response.status(422)
                .header("Wrong-Param", "TipoDescuento")
                .build();
    }

    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response editar(
            @PathParam("id")
            UUID id,

            TipoDescuento modificado) {

        if (id != null && modificado != null) {
            try {
                TipoDescuento encontrado = tdRepo.findById(id);

                if (encontrado != null) {
                    modificado.setIdTipoDescuento(id);
                    tdRepo.modificar(modificado);

                    return Response.ok(modificado).build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Entity-Not-Found", id)
                        .build();

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);

                return Response.status(500)
                        .header("App-Error", e.getMessage())
                        .build();
            }
        }

        return Response.status(422)
                .header("Wrong-Param", "TipoDescuento")
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                TipoDescuento encontrado = tdRepo.findById(id);

                if (encontrado != null) {
                    tdRepo.eliminar(encontrado);

                    return Response.noContent().build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Entity-Not-Found", id)
                        .build();

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);

                return Response.status(500)
                        .header("App-Error", e.getMessage())
                        .build();
            }
        }

        return Response.status(422)
                .header("Wrong-Param", "TipoDescuento")
                .build();
    }
}
