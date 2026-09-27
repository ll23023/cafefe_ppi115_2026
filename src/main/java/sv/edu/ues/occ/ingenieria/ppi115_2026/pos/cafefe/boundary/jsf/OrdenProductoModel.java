package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Orden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.OrdenProducto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class OrdenProductoModel extends DefaultModel<OrdenProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private OrdenProductoRepository ordenProductoRepository;

    @Inject
    private OrdenRepository ordenRepository;

    private List<Orden> ordenes;
    private List<ColumnaTabla> columnas;

    @Override
    public OrdenProductoRepository getRepository() {
        return ordenProductoRepository;
    }

    @Override
    public OrdenProducto instanciarRegistro() {
        OrdenProducto r = new OrdenProducto(UUID.randomUUID());
        r.setObservaciones("");
        return r;
    }

    @Override
    public OrdenProducto getRegistroById(Object id) {
        return id == null ? null : ordenProductoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(OrdenProducto dato) {
        return dato != null ? dato.getIdOrdenProducto() : null;
    }

    /**
     * Combo de Orden, por si se arma una pantalla propia para OrdenProducto
     * más adelante. El combo de Producto todavía no se puede armar porque
     * falta esa entidad/repositorio.
     */
    public List<Orden> getOrdenes() {
        if (this.ordenes == null) {
            this.ordenes = ordenRepository != null ? ordenRepository.findAll() : new ArrayList<>();
        }
        return ordenes;
    }

    public void setOrdenes(List<Orden> ordenes) {
        this.ordenes = ordenes;
    }

    /**
     * Columnas para tablaDinamica. La columna "Producto" queda como
     * placeholder (muestra el toString() de Producto) hasta tener esa
     * entidad con un campo de nombre real.
     */
    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("idOrden.fechaCreacion", "Fecha de la orden", FORMATO_COLUMNA.DATE),
                    new ColumnaTabla("idProducto", "Producto"),
                    new ColumnaTabla("precio", "Precio", FORMATO_COLUMNA.CURRENCY),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}