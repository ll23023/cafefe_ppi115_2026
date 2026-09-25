package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CajaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caja;

/**
 *
 * @author mikelb
 */
@Named
@ViewScoped
public class CajaModel extends DefaultModel<Caja, UUID> {

    @Inject
    private CajaRepository cajaRepository;

    @Override
    public CajaRepository getRepository() {
        return cajaRepository;
    }

    @Override
    public Caja instanciarRegistro() {
        Caja caja = new Caja(UUID.randomUUID());
        caja.setActivo(Boolean.TRUE);
        caja.setObservaciones("");
        return caja;
    }

    @Override
    public Caja getRegistroById(Object id) {
        return id == null ? null : cajaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Caja dato) {
        return dato != null ? dato.getIdCaja() : null;
    }
}
