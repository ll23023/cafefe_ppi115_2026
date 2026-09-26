package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class RolModel extends DefaultModel<Rol, UUID>{

    private static final long serialVersionUID = 1L;

    @Inject
    private RolRepository rolRepository;

     @Override
    public RolRepository getRepository() {
        return rolRepository;
    }

    @Override
    public Rol instanciarRegistro() {
        Rol r = new Rol(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        return r;
    }

    @Override
    public Rol getRegistroById(Object id) {
        return id == null ? null : rolRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Rol dato) {
        return dato != null ? dato.getIdRol() : null;
    }

}
