package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Descuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.DescuentoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;

@Named
@ViewScoped
public class DescuentoProductoModel extends DefaultModel<DescuentoProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private DescuentoProductoRepository descuentoProductoRepository;
    @Inject
    private DescuentoRepository descuentoRepository;
    @Inject
    private ProductoRepository productoRepository;

    private List<Descuento> descuentos;
    private List<Producto> productos;
    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("idDescuentoProducto", "ID"),
            new ColumnaTabla("idDescuento.nombre", "Descuento"),
            new ColumnaTabla("idProducto.nombre", "Producto"),
            new ColumnaTabla("fechaDesde", "Desde", FORMATO_COLUMNA.DATE),
            new ColumnaTabla("fechaHasta", "Hasta", FORMATO_COLUMNA.DATE),
            new ColumnaTabla("valor", "Valor"),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    @Override
    public DescuentoProductoRepository getRepository() {
        return descuentoProductoRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        descuentos = descuentoRepository.findAll();
        productos = productoRepository.findAll();
    }

    @Override
    public DescuentoProducto instanciarRegistro() {
        return new DescuentoProducto(UUID.randomUUID());
    }

    @Override
    public DescuentoProducto getRegistroById(Object id) {
        return id == null ? null : descuentoProductoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(DescuentoProducto dato) {
        return dato == null ? null : dato.getIdDescuentoProducto();
    }

    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}
