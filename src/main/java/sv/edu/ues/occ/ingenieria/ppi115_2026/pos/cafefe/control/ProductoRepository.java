package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;

@Stateless
@LocalBean
public class ProductoRepository extends DefaultRepository<Producto, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public ProductoRepository() {
        super(Producto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Producto> findActivos() {
        return getEntityManager()
                .createNamedQuery("Producto.findActivos", Producto.class)
                .getResultList();
    }

    public List<Producto> findByNombreLike(String nombre) {
        return getEntityManager()
                .createNamedQuery("Producto.findByNombreLike", Producto.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    public List<Producto> findByPrecioRange(BigDecimal min, BigDecimal max) {
        return getEntityManager()
                .createNamedQuery("Producto.findByPrecioRange", Producto.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }
}
