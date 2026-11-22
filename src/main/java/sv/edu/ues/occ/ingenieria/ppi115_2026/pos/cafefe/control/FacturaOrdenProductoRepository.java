package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
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

    /**
     * Líneas de una factura, paginadas.
     */
    public List<FacturaOrdenProducto> findByFactura(UUID idFactura, int first, int max) {
        requireArgument(idFactura, "idFactura");
        requirePage(first, max);
        return requireEntityManager()
                .createQuery("SELECT f FROM FacturaOrdenProducto f WHERE f.idFactura.idFactura = :id", FacturaOrdenProducto.class)
                .setParameter("id", idFactura)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Cantidad de líneas de una factura.
     */
    public Long countByFactura(UUID idFactura) {
        requireArgument(idFactura, "idFactura");
        return requireEntityManager()
                .createQuery("SELECT COUNT(f) FROM FacturaOrdenProducto f WHERE f.idFactura.idFactura = :id", Long.class)
                .setParameter("id", idFactura)
                .getSingleResult();
    }

    /**
     * Cuántas veces ya está incluido un orden_producto en una factura.
     */
    public long countByFacturaAndOrdenProducto(UUID idFactura, UUID idOrdenProducto) {
        requireArgument(idFactura, "idFactura");
        requireArgument(idOrdenProducto, "idOrdenProducto");
        return requireEntityManager()
                .createQuery("SELECT COUNT(f) FROM FacturaOrdenProducto f "
                        + "WHERE f.idFactura.idFactura = :fac AND f.idOrdenProducto.idOrdenProducto = :op", Long.class)
                .setParameter("fac", idFactura)
                .setParameter("op", idOrdenProducto)
                .getSingleResult();
    }
}