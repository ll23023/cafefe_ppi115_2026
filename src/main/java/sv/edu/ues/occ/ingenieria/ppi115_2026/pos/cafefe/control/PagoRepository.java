package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Pago;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class PagoRepository extends DefaultRepository<Pago, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public PagoRepository() {
        super(Pago.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Pagos de una factura, paginados.
     */
    public List<Pago> findByFactura(UUID idFactura, int first, int max) {
        return getEntityManager()
                .createQuery("SELECT p FROM Pago p WHERE p.idFactura.idFactura = :id", Pago.class)
                .setParameter("id", idFactura)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Cantidad de pagos de una factura.
     */
    public Long countByFactura(UUID idFactura) {
        return getEntityManager()
                .createQuery("SELECT COUNT(p) FROM Pago p WHERE p.idFactura.idFactura = :id", Long.class)
                .setParameter("id", idFactura)
                .getSingleResult();
    }

    /**
     * Cantidad de detalles de pago que referencian este pago.
     */
    public long countDetalles(UUID idPago) {
        return getEntityManager()
                .createQuery("SELECT COUNT(d) FROM PagoDetalle d WHERE d.idPago.idPago = :id", Long.class)
                .setParameter("id", idPago)
                .getSingleResult();
    }
}