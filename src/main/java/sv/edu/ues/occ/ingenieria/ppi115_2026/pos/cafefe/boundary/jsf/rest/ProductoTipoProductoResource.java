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
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoTipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoTipoProducto;

@Path("producto_tipo_producto")
public class ProductoTipoProductoResource implements Serializable {

    @Inject
    private ProductoTipoProductoRepository repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(@QueryParam("desde") @DefaultValue("1") int desde,
            @QueryParam("hasta") @DefaultValue("50") int hasta) {
        if (!(0 < desde && desde < hasta)) return Response.status(422).header("Wrong-Param", "cantidad").build();
        try { return Response.ok(repository.findRange(desde, hasta)).build(); }
        catch (Exception exception) { return error(exception); }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id == null) return Response.status(422).build();
        try {
            ProductoTipoProducto entidad = repository.findById(id);
            return entidad == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(entidad).build();
        } catch (Exception exception) { return error(exception); }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(ProductoTipoProducto entidad, @Context UriInfo uriInfo) {
        if (entidad == null) return Response.status(422).build();
        try {
            entidad.setIdProductoTipoProducto(UUID.randomUUID());
            repository.crear(entidad);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(entidad.getIdProductoTipoProducto().toString()).build()).build();
        } catch (Exception exception) { return error(exception); }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, ProductoTipoProducto modificado) {
        if (id == null || modificado == null) return Response.status(422).build();
        try {
            if (repository.findById(id) == null) return Response.status(Response.Status.NOT_FOUND).build();
            modificado.setIdProductoTipoProducto(id);
            return Response.ok(repository.modificar(modificado)).build();
        } catch (Exception exception) { return error(exception); }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (id == null) return Response.status(422).build();
        try {
            ProductoTipoProducto entidad = repository.findById(id);
            if (entidad == null) return Response.status(Response.Status.NOT_FOUND).build();
            repository.eliminar(entidad);
            return Response.noContent().build();
        } catch (Exception exception) { return error(exception); }
    }

    private Response error(Exception exception) {
        return Response.serverError().header("App-Error", exception.getMessage()).build();
    }
}
