package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
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
        TipoProducto r = new TipoProducto(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        return r;
    }

    @Override
    public TipoProducto getRegistroById(Object id) {
        if (id != null && this.registros != null && !this.registros.isEmpty()) {
            UUID busca = (UUID) id;
            return this.registros.stream()
                    .filter(r -> r.getIdTipoProducto().equals(busca))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Object getIdByRegistro(TipoProducto dato) {
        if (dato != null) {
            return dato.getIdTipoProducto();
        }
        return null;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}
