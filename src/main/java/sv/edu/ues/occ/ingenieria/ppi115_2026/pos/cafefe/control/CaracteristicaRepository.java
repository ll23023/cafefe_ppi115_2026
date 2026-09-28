package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caracteristica;

@Stateless
@LocalBean
public class CaracteristicaRepository extends DefaultRepository<Caracteristica, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public CaracteristicaRepository() {
        super(Caracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
