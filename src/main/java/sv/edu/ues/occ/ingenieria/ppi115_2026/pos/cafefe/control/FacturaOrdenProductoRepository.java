package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.FacturaOrdenProducto;

/**
 * 
 * @author mil
 */
@Stateless
public class FacturaOrdenProductoRepository extends DefaultRepository<FacturaOrdenProducto, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public FacturaOrdenProductoRepository() {
        super(FacturaOrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}