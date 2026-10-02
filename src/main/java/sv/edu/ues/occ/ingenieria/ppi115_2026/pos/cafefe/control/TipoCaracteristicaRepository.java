package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class TipoCaracteristicaRepository extends DefaultRepository<TipoCaracteristica, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public TipoCaracteristicaRepository() {
        super(TipoCaracteristica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}