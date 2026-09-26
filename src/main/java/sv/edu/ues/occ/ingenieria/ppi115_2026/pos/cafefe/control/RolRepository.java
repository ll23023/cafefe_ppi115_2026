package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class RolRepository extends DefaultRepository<Rol, java.util.UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public RolRepository() {
        super(Rol.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Rol> findByNombre(String nombre) {
        return getEntityManager()
                .createNamedQuery("Rol.findByNombre", Rol.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    /**
     * Elimina un Rol re-atachándolo primero al EntityManager actual. Esto evita
     * "Removing a detached instance" cuando el objeto viene desde el
     * @ViewScoped (fuera de la transacción que lo cargó).
     */
    @Override
    public void eliminar(Rol registro) {
        if (registro == null || registro.getIdRol() == null) {
            return;
        }
        Rol managed = getEntityManager().contains(registro)
                ? registro
                : getEntityManager().find(Rol.class, registro.getIdRol());

        if (managed != null) {
            getEntityManager().remove(managed);
        }
    }
}
