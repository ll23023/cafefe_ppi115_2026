package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Orden;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class OrdenRepository extends DefaultRepository<Orden, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public OrdenRepository() {
        super(Orden.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}