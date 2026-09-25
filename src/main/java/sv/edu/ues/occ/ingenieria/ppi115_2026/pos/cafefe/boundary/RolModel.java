package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class RolModel extends DefaultModel<Rol, UUID> implements Serializable {

    @Inject
    private RolRepository rolRepository;

    public RolModel() {
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
        return rolRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Rol dato) {
        return dato != null ? dato.getIdRol() : null;
    }

    @Override
    public RolRepository getDao() {
        return rolRepository;
    }

    public void buscarPorNombre(String nombre) {
        this.registros = rolRepository.findByNombre(nombre);
    }
}
