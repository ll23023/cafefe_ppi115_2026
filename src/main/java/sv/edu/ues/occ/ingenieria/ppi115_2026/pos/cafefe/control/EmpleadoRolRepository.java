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

}