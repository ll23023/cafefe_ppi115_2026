package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class TipoDescuentoRepository extends DefaultRepository<TipoDescuento, UUID> {

    @PersistenceContext(unitName = "cafefe-PU")
    private EntityManager em;

    public TipoDescuentoRepository() {
        super(TipoDescuento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}

