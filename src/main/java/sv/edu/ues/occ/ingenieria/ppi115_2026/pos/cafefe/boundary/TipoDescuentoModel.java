package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento;

/**
 *
 * @author mil
 */
@Named("tipoDescuentoModel")
@ViewScoped

public class TipoDescuentoModel extends DefaultModel<TipoDescuento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoDescuentoRepository tipoDescuentoRepository;

    @Override
    public TipoDescuento instanciarRegistro() {
        TipoDescuento r = new TipoDescuento(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        r.setDescuentoMaximo(0);
        return r;
    }

    @Override
    public TipoDescuento getRegistroById(Object id) {
        if (id != null && this.registros != null && !this.registros.isEmpty()) {
            UUID busca = (UUID) id;
            return this.registros.stream()
                    .filter(r -> r.getIdTipoDescuento().equals(busca))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Object getIdByRegistro(TipoDescuento dato) {
        if (dato != null) {
            return dato.getIdTipoDescuento();
        }
        return null;
    }

    @Override
    public TipoDescuentoRepository getRepository() {
        return tipoDescuentoRepository;
    }

}
