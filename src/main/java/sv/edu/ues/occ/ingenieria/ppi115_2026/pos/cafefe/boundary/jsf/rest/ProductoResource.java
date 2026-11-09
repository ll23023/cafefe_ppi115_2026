package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.*;

/**
 *
 * @author mil
 */
@Path("producto")
public class ProductoResource implements Serializable {

    private static final Logger LOG = Logger.getLogger(ProductoResource.class.getName());

    @Inject
    jakarta.persistence.EntityManager em;
    @Inject
    ProductoRepository productoRepository;
    @Inject
    ProductoCaracteristicaRepository productoCaracteristicaRepository;
    @Inject
    ProductoTipoProductoRepository productoTipoProductoRepository;
    @Inject
    DescuentoProductoRepository descuentoProductoRepository;

    @Inject
    CaracteristicaRepository caracteristicaRepository;
    @Inject
    TipoCaracteristicaRepository tipoCaracteristicaRepository;
    @Inject
    TipoProductoRepository tipoProductoRepository;
    @Inject
    DescuentoRepository descuentoRepository;
    @Inject
    TipoDescuentoRepository tipoDescuentoRepository;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("first") @DefaultValue("0") int first, @QueryParam("max") @DefaultValue("50") int max) {
        try {
            if (first >= 0 && max > 0 && max <= 50) {
                List<Producto> productos = productoRepository.findRange(first, max);
                Long total = productoRepository.count();
                return Response.ok(productos).header(ResponseHeaders.TOTAL_REGISTROS.toString(), total).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).header(ResponseHeaders.WRONG_PARAMETER.toString(), "Parámetros inválidos").build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Producto encontrado = productoRepository.findById(id);
                if (encontrado != null) {
                    return Response.ok(encontrado).build();
                }
                return Response.status(Response.Status.NOT_FOUND).header(ResponseHeaders.NOT_FOUND.toString(), "id: " + id).build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage()).build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).header(ResponseHeaders.WRONG_PARAMETER.toString(), "id nulo").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(ProductoRequest request, @Context UriInfo uriInfo) {
        if (request == null || request.nombre == null || request.nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).header(ResponseHeaders.WRONG_PARAMETER.toString(), "El nombre es obligatorio").build();
        }
        try {
            if (request.precioSugerido == null || request.precioSugerido.doubleValue() < 0 || request.precioSugerido.doubleValue() > 50) {
                return Response.status(Response.Status.BAD_REQUEST).header(ResponseHeaders.WRONG_PARAMETER.toString(), "El precio debe estar entre 0 y 50").build();
            }

            Producto nuevo = new Producto(UUID.randomUUID());
            nuevo.setNombre(request.nombre.trim());
            nuevo.setActivo(request.activo != null ? request.activo : true);
            nuevo.setComentarios(request.comentarios != null ? request.comentarios : "");
            nuevo.setPrecioSugerido(request.precioSugerido);

            productoRepository.crear(nuevo);

            // Sincronizar Relaciones
            procesarCaracteristicas(nuevo, request.caracteristicas);
            procesarTiposProducto(nuevo, request.tiposProducto);
            procesarDescuentos(nuevo, request.descuentos);

            URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(nuevo.getIdProducto())).build();
            return Response.created(location).build();

        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.CONFLICT).header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), ex.getMessage()).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage()).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@PathParam("id") UUID id, ProductoRequest request) {
        if (id == null || request == null) {
            return Response.status(Response.Status.BAD_REQUEST).header(ResponseHeaders.WRONG_PARAMETER.toString(), "Datos inválidos").build();
        }
        try {
            Producto existente = productoRepository.findById(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            existente.setNombre(request.nombre.trim());
            existente.setActivo(request.activo != null ? request.activo : existente.getActivo());
            existente.setComentarios(request.comentarios != null ? request.comentarios : "");
            if (request.precioSugerido != null) {
                existente.setPrecioSugerido(request.precioSugerido);
            }

            productoRepository.modificar(existente);

            // limpiar las relaciones antiguas y volver a insertar
            // o hacer una lógica de comparación. Para simplificar, si se envia nuevas, se añaden.
            procesarCaracteristicas(existente, request.caracteristicas);
            procesarTiposProducto(existente, request.tiposProducto);
            procesarDescuentos(existente, request.descuentos);

            return Response.ok(existente).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.CONFLICT).header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), ex.getMessage()).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(ResponseHeaders.PROCESS_ERROR.toString(), ex.getMessage()).build();
        }
    }

    //no permite eliminar productos que tenga relacionado ya con alguna orden
    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            Producto encontrado = productoRepository.findById(id);
            if (encontrado == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            //Revisar asociaciones que impiden borrar ANTES de borrar nada
            Long totalOrdenes = em.createQuery(
                    "SELECT COUNT(op) FROM OrdenProducto op WHERE op.idProducto = :p", Long.class)
                    .setParameter("p", encontrado)
                    .getSingleResult();

            if (totalOrdenes > 0) {
                String motivo = "No se pudo eliminar el producto '" + encontrado.getNombre()
                        + "' porque está asociado a " + totalOrdenes + " orden(es).";
                return Response.status(Response.Status.CONFLICT)
                        .header(ResponseHeaders.VIOLATES_BUSINESS_RULES.toString(), motivo)
                        .entity("{\"error\":\"" + motivo + "\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            //borrar dependencias y el producto
            productoCaracteristicaRepository.findByProducto(id).forEach(productoCaracteristicaRepository::eliminar);
            productoTipoProductoRepository.findByProducto(id).forEach(productoTipoProductoRepository::eliminar);
            descuentoProductoRepository.findByProducto(id).forEach(descuentoProductoRepository::eliminar);
            productoRepository.eliminar(encontrado);

            return Response.noContent().build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar producto: " + id, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header(ResponseHeaders.PROCESS_ERROR.toString(), "Error interno: " + ex.getMessage()).build();
        }
    }

    private void procesarCaracteristicas(Producto producto, List<ProductoRequest.CaracteristicaDto> dtos) {
        if (dtos == null) {
            return;
        }
        for (ProductoRequest.CaracteristicaDto dto : dtos) {
            // Buscar o crear característica
            Caracteristica carac = caracteristicaRepository.findAll().stream()
                    .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(dto.nombre))
                    .findFirst().orElse(null);

            if (carac == null) {
                carac = new Caracteristica(UUID.randomUUID());
                carac.setNombre(dto.nombre);
                carac.setActivo(true);
                carac.setObservaciones("");

                // Buscar el tipo
                TipoCaracteristica tipo = tipoCaracteristicaRepository.findAll().stream()
                        .filter(t -> t.getNombre() != null && t.getNombre().equalsIgnoreCase(dto.tipo))
                        .findFirst().orElse(null);
                if (tipo == null) {
                    throw new IllegalArgumentException("El tipo de característica no existe: " + dto.tipo);
                }
                carac.setIdTipoCaracteristica(tipo);

                caracteristicaRepository.crear(carac);
            }

            if (carac.getIdTipoCaracteristica() != null && carac.getIdTipoCaracteristica().getExpresionRegular() != null) {
                String regex = carac.getIdTipoCaracteristica().getExpresionRegular();
                if (!regex.isBlank() && !Pattern.matches(regex, dto.valor == null ? "" : dto.valor)) {
                    throw new IllegalArgumentException("El valor '" + dto.valor + "' no cumple con la expresión regular del tipo " + dto.tipo);
                }
            }

            ProductoCaracteristica pc = new ProductoCaracteristica(UUID.randomUUID());
            pc.setIdProducto(producto);
            pc.setIdCaracteristica(carac);
            pc.setValor(dto.valor);
            productoCaracteristicaRepository.crear(pc);
        }
    }

    private void procesarTiposProducto(Producto producto, List<String> tiposNombres) {
        if (tiposNombres == null) {
            return;
        }
        for (String nombreTipo : tiposNombres) {
            TipoProducto tipo = tipoProductoRepository.findAll().stream()
                    .filter(t -> t.getNombre() != null && t.getNombre().equalsIgnoreCase(nombreTipo))
                    .findFirst().orElse(null);
            if (tipo == null) {
                tipo = new TipoProducto(UUID.randomUUID());
                tipo.setNombre(nombreTipo);
                tipo.setActivo(true);
                tipoProductoRepository.crear(tipo);
            }

            ProductoTipoProducto ptp = new ProductoTipoProducto(UUID.randomUUID());
            ptp.setIdProducto(producto);
            ptp.setIdTipoProducto(tipo);
            productoTipoProductoRepository.crear(ptp);
        }
    }

    private void procesarDescuentos(Producto producto, List<ProductoRequest.DescuentoDto> dtos) {
        if (dtos == null) {
            return;
        }
        for (ProductoRequest.DescuentoDto dto : dtos) {
            if (dto.nombre == null || dto.valor == null) {
                continue;
            }

            Descuento desc = descuentoRepository.findAll().stream()
                    .filter(d -> d.getNombre() != null && d.getNombre().equalsIgnoreCase(dto.nombre))
                    .findFirst().orElse(null);

            if (desc == null) {
                desc = new Descuento(UUID.randomUUID());
                desc.setNombre(dto.nombre);
                desc.setFechaDesde(dto.fechaDesde);
                desc.setFechaHasta(dto.fechaHasta);

                TipoDescuento tipo = tipoDescuentoRepository.findAll().stream()
                        .filter(t -> t.getNombre() != null && t.getNombre().equalsIgnoreCase(dto.tipoDescuento))
                        .findFirst().orElse(null);
                if (tipo == null) {
                    throw new IllegalArgumentException("El tipo de descuento no existe: " + dto.tipoDescuento);
                }

                if (dto.valor > tipo.getDescuentoMaximo()) {
                    throw new IllegalArgumentException("El valor del descuento supera el máximo de " + tipo.getDescuentoMaximo() + "%");
                }
                desc.setIdTipoDescuento(tipo);
                descuentoRepository.crear(desc);
            }

            DescuentoProducto dp = new DescuentoProducto(UUID.randomUUID());
            dp.setIdProducto(producto);
            dp.setIdDescuento(desc);
            dp.setValor(dto.valor);
            dp.setFechaDesde(dto.fechaDesde);
            dp.setFechaHasta(dto.fechaHasta);
            descuentoProductoRepository.crear(dp);
        }
    }
}
