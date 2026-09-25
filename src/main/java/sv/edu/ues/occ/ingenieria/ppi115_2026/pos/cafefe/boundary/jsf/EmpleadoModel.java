package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;

/**
 *
 * @author mikelb
 */
@Named
@ViewScoped
public class EmpleadoModel extends DefaultModel<Empleado, UUID> {
    
    private static final long serialVersionUID = 1L;

    @Inject
    private EmpleadoRepository empleadoRepository;

    @Override
    public EmpleadoRepository getRepository() {
        return empleadoRepository;
    }

    @Override
    public Empleado instanciarRegistro() {
        Empleado r = new Empleado(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setComentarios("");
        return r;
    }

    @Override
    public Empleado getRegistroById(Object id) {
        if (id != null && this.registros != null && !this.registros.isEmpty()) {
            UUID busca = (UUID) id;
            return this.registros.stream()
                    .filter(r -> r.getIdEmpleado().equals(busca))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Object getIdByRegistro(Empleado dato) {
        if (dato != null) {
            return dato.getIdEmpleado();
        }
        return null;
    }

}
