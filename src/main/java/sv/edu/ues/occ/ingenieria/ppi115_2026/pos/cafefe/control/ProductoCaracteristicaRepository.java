package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoCaracteristica;

@Stateless
@LocalBean
public class ProductoCaracteristicaRepository extends DefaultRepository<ProductoCaracteristica, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public ProductoCaracteristicaRepository() {
        super(ProductoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
