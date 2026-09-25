package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

@Named
@ViewScoped
public class RolModel extends DefaultModel<Rol, UUID> implements Serializable {

    @Inject
    private RolRepository rolRepository;

    public RolModel() {
    }

    @Override
    public Rol instanciarRegistro() {
        return new Rol();
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
    public RolRepository getRepository() {
        return rolRepository;
    }

    public void buscarPorNombre(String nombre) {
        this.registros = rolRepository.findByNombre(nombre);
    }
}