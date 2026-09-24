package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

@Stateless
@LocalBean
public class RolRepository extends DefaultRepository<Rol, java.util.UUID> {

    @PersistenceContext(unitName = "Cafefe-PU") 
    private EntityManager em;

    public RolRepository() {
        super(Rol.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Rol> findByNombre(String nombre) {
        return getEntityManager()
                .createNamedQuery("Rol.findByNombre", Rol.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }
}