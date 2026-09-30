package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoTipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoTipoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class ProductoTipoProductoModel extends DefaultModel<ProductoTipoProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoTipoProductoRepository productoTipoProductoRepository;

    @Inject
    private ProductoRepository productoRepository;

    @Inject
    private TipoProductoRepository tipoProductoRepository;

    private List<Producto> productos;
    private List<TipoProducto> tiposProducto;
    private List<ColumnaTabla> columnas;

    @Override
    public ProductoTipoProductoRepository getRepository() {
        return productoTipoProductoRepository;
    }

    @Override
    public ProductoTipoProducto instanciarRegistro() {
        ProductoTipoProducto r = new ProductoTipoProducto(UUID.randomUUID());
        r.setFechaCreacion(new Date());
        r.setObservaciones("");
        return r;
    }

    @Override
    public ProductoTipoProducto getRegistroById(Object id) {
        return id == null ? null : productoTipoProductoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(ProductoTipoProducto dato) {
        return dato != null ? dato.getIdProductoTipoProducto() : null;
    }

    // Carga perezosa para el combo de Producto (no tiene restricción de activo).
    public List<Producto> getProductos() {
        if (this.productos == null) {
            this.productos = productoRepository != null ? productoRepository.findAll() : new ArrayList<>();
        }
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<TipoProducto> getTiposProducto() {
        if (this.tiposProducto == null) {
            this.tiposProducto = tipoProductoRepository != null
                    ? tipoProductoRepository.findAll().stream()
                            .filter(t -> Boolean.TRUE.equals(t.getActivo()))
                            .toList()
                    : new ArrayList<>();
        }
        return tiposProducto;
    }

    public void setTiposProducto(List<TipoProducto> tiposProducto) {
        this.tiposProducto = tiposProducto;
    }

    @Override
    public void btnGuardarHandler() {
        if (this.registro != null
                && this.registro.getIdTipoProducto() != null
                && !Boolean.TRUE.equals(this.registro.getIdTipoProducto().getActivo())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se puede asignar un tipo de producto inactivo.", null));
            return;
        }
        super.btnGuardarHandler();
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("idProducto.nombre", "Producto"),
                    new ColumnaTabla("idTipoProducto.nombre", "Tipo"),
                    new ColumnaTabla("fechaCreacion", "Fecha", FORMATO_COLUMNA.DATE),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}