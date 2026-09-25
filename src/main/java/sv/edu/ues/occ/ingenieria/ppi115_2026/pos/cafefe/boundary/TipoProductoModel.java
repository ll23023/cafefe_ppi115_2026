package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class TipoProductoModel  extends DefaultModel<TipoProducto, UUID> implements Serializable {

   private static final long serialVersionUID = 1L;

    @Inject
    private TipoProductoRepository tipoProductoRepository;

    @Override
    public DefaultRepository<TipoProducto, UUID> getDao() {
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
}
