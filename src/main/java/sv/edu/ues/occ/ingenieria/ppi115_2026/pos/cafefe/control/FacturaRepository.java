package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class FacturaRepository extends DefaultRepository<Factura, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public FacturaRepository() {
        super(Factura.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Cantidad de pagos de la factura.
     */
    public long countPagos(UUID idFactura) {
        return getEntityManager()
                .createQuery("SELECT COUNT(p) FROM Pago p WHERE p.idFactura.idFactura = :id", Long.class)
                .setParameter("id", idFactura)
                .getSingleResult();
    }

    /**
     * Cantidad de líneas (factura_orden_producto) de la factura.
     */
    public long countLineas(UUID idFactura) {
        return getEntityManager()
                .createQuery("SELECT COUNT(f) FROM FacturaOrdenProducto f WHERE f.idFactura.idFactura = :id", Long.class)
                .setParameter("id", idFactura)
                .getSingleResult();
    }
}