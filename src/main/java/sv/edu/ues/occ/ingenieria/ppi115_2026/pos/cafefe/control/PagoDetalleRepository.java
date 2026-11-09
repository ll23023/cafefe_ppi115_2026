package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.PagoDetalle;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class PagoDetalleRepository extends DefaultRepository<PagoDetalle, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public PagoDetalleRepository() {
        super(PagoDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Detalles de un pago, paginados.
     */
    public List<PagoDetalle> findByPago(UUID idPago, int first, int max) {
        return getEntityManager()
                .createQuery("SELECT d FROM PagoDetalle d WHERE d.idPago.idPago = :id", PagoDetalle.class)
                .setParameter("id", idPago)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Cantidad de detalles de un pago.
     */
    public Long countByPago(UUID idPago) {
        return getEntityManager()
                .createQuery("SELECT COUNT(d) FROM PagoDetalle d WHERE d.idPago.idPago = :id", Long.class)
                .setParameter("id", idPago)
                .getSingleResult();
    }
}