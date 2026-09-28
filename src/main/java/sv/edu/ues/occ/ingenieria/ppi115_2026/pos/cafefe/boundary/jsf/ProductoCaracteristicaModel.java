package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoCaracteristica;

@Named
@ViewScoped
public class ProductoCaracteristicaModel extends DefaultModel<ProductoCaracteristica, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoCaracteristicaRepository productoCaracteristicaRepository;
    @Inject
    private ProductoRepository productoRepository;
    @Inject
    private CaracteristicaRepository caracteristicaRepository;

    private List<Producto> productos;
    private List<Caracteristica> caracteristicas;
    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("idProductoCaracteristica", "ID"),
            new ColumnaTabla("idProducto.nombre", "Producto"),
            new ColumnaTabla("idCaracteristica.nombre", "Característica"),
            new ColumnaTabla("valor", "Valor")
    );

    @Override
    public ProductoCaracteristicaRepository getRepository() {
        return productoCaracteristicaRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        productos = productoRepository.findAll();
        caracteristicas = caracteristicaRepository.findAll();
    }

    @Override
    public ProductoCaracteristica instanciarRegistro() {
        return new ProductoCaracteristica(UUID.randomUUID());
    }

    @Override
    public ProductoCaracteristica getRegistroById(Object id) {
        return id == null ? null : productoCaracteristicaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(ProductoCaracteristica dato) {
        return dato == null ? null : dato.getIdProductoCaracteristica();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<Caracteristica> getCaracteristicas() {
        return caracteristicas;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}
