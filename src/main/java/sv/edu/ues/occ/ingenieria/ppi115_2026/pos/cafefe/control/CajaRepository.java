package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caja;

/**
 *
 * @author mikelb
 */
@Stateless
@LocalBean
public class CajaRepository extends DefaultRepository<Caja, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public CajaRepository() {
        super(Caja.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
