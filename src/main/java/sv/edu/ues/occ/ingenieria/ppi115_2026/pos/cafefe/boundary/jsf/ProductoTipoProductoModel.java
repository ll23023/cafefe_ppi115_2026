package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoTipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoTipoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

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
    @PostConstruct
    public void init() {
        super.init();
        productos = productoRepository.findAll();
        tiposProducto = tipoProductoRepository.findAll();
    }

    @Override
    public ProductoTipoProducto instanciarRegistro() {
        return new ProductoTipoProducto(UUID.randomUUID());
    }

    @Override
    public ProductoTipoProducto getRegistroById(Object id) {
        return id == null ? null : productoTipoProductoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(ProductoTipoProducto dato) {
        return dato == null ? null : dato.getIdProductoTipoProducto();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<TipoProducto> getTiposProducto() {
        return tiposProducto;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null)
            columnas = List.of(new ColumnaTabla("idProductoTipoProducto", "ID"),
                    new ColumnaTabla("idProducto.nombre", "Producto"),
                    new ColumnaTabla("idTipoProducto.nombre", "Tipo de producto"),
                    new ColumnaTabla("fechaCreacion", "Fecha de creación", FORMATO_COLUMNA.DATE),
                    new ColumnaTabla("observaciones", "Observaciones"));
        return columnas;
    }
}
