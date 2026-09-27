package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class EmpleadoRolRepository extends DefaultRepository<EmpleadoRol, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public EmpleadoRolRepository() {
        super(EmpleadoRol.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Elimina un EmpleadoRol re-atachándolo primero al EntityManager actual.
     * Esto evita "Removing a detached instance" cuando el objeto viene desde
     * el @ViewScoped (fuera de la transacción que lo cargó), algo más
     * probable aquí por las relaciones @ManyToOne con Empleado y Rol.
     */
    @Override
    public void eliminar(EmpleadoRol registro) {
        if (registro == null || registro.getIdEmpleadoRol() == null) {
            return;
        }
        EmpleadoRol managed = getEntityManager().contains(registro)
                ? registro
                : getEntityManager().find(EmpleadoRol.class, registro.getIdEmpleadoRol());

        if (managed != null) {
            getEntityManager().remove(managed);
        }
    }
}