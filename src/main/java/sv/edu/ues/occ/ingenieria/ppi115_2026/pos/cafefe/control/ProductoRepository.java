package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;

/**
 *
 * @author mil
 */
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

    public List<Producto> findByNombre(String nombre) {
        return getEntityManager()
                .createNamedQuery("Producto.findByNombre", Producto.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

}
