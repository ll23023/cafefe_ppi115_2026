package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class TipoProductoModel extends DefaultModel<TipoProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoProductoRepository tipoProductoRepository;

    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("idTipoProducto", "ID"),
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    @Override
    public TipoProductoRepository getRepository() {
        return tipoProductoRepository;
    }

    @Override
    public TipoProducto instanciarRegistro() {
        TipoProducto t = new TipoProducto(UUID.randomUUID());
        t.setActivo(Boolean.TRUE);
        t.setObservaciones("");
        return t;
    }

    @Override
    public TipoProducto getRegistroById(Object id) {
        return id == null ? null : tipoProductoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(TipoProducto dato) {
        return dato != null ? dato.getIdTipoProducto() : null;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}