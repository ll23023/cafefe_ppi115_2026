package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;

/**
 *
 * @author mil
 */
@Stateless
@LocalBean
public class EmpleadoRolRepository extends DefaultRepository<EmpleadoRol, UUID> {

    @PersistenceContext(unitName = "Cafefe-PU")
    private EntityManager em;

    public EmpleadoRolRepository() {
        super(EmpleadoRol.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Asignaciones de rol que tiene un empleado.
     */
    public List<EmpleadoRol> findByEmpleado(UUID idEmpleado) {
        return getEntityManager()
                .createQuery("SELECT er FROM EmpleadoRol er WHERE er.idEmpleado.idEmpleado = :id", EmpleadoRol.class)
                .setParameter("id", idEmpleado)
                .getResultList();
    }

    /**
     * Cantidad de facturas que referencian esta asignación.
     */
    public long countFacturas(UUID idEmpleadoRol) {
        return getEntityManager()
                .createQuery("SELECT COUNT(f) FROM Factura f WHERE f.idEmpleadoRol.idEmpleadoRol = :id", Long.class)
                .setParameter("id", idEmpleadoRol)
                .getSingleResult();
    }

    /**
     * Cantidad de órdenes que referencian esta asignación.
     */
    public long countOrdenes(UUID idEmpleadoRol) {
        return getEntityManager()
                .createQuery("SELECT COUNT(o) FROM Orden o WHERE o.idEmpleadoRol.idEmpleadoRol = :id", Long.class)
                .setParameter("id", idEmpleadoRol)
                .getSingleResult();
    }
}