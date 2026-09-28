package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class ProductoModel extends DefaultModel<Producto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoRepository productoRepository;
    private List<ColumnaTabla> columnas;

    @Override
    public ProductoRepository getRepository() {
        return productoRepository;
    }

    @Override
    public Producto instanciarRegistro() {
        Producto p = new Producto(UUID.randomUUID());
        p.setActivo(Boolean.TRUE);
        p.setComentarios("");
        return p;
    }

    @Override
    public Producto getRegistroById(Object id) {
        return id == null ? null : productoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Producto dato) {
        return dato != null ? dato.getIdProducto() : null;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("precioSugerido", "Precio sugerido", FORMATO_COLUMNA.CURRENCY),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("comentarios", "Comentarios"));
        }
        return columnas;
    }
}