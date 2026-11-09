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

    /**
     * Cantidad de productos (orden_producto) que tiene la orden.
     */
    public long countOrdenProductos(UUID idOrden) {
        return getEntityManager()
                .createQuery("SELECT COUNT(op) FROM OrdenProducto op WHERE op.idOrden.idOrden = :id", Long.class)
                .setParameter("id", idOrden)
                .getSingleResult();
    }

    /**
     * Cantidad de líneas de factura que facturan productos de esta orden.
     */
    public long countLineasFacturadas(UUID idOrden) {
        return getEntityManager()
                .createQuery("SELECT COUNT(f) FROM FacturaOrdenProducto f WHERE f.idOrdenProducto.idOrden.idOrden = :id", Long.class)
                .setParameter("id", idOrden)
                .getSingleResult();
    }
}