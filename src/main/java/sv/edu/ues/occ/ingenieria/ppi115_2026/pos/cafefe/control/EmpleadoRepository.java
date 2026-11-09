package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;
import java.util.UUID;

/**
 *
 * @author mikelb
 */
@Stateless
@LocalBean
public class EmpleadoRepository extends DefaultRepository<Empleado, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;
    
    
    public EmpleadoRepository() {
        super(Empleado.class);
    }
    
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
}
