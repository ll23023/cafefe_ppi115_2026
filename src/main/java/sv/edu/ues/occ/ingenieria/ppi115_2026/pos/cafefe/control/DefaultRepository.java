package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author mikelb
 * @param <T>  para la entidad
 * @param <ID> para el tipo de dato del ID
 */
public abstract class DefaultRepository<T,ID> implements RepositoryInterface<T, ID>{
     private final Class<T> entityClass;

    public DefaultRepository(Class<T> entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("entityClass no puede ser null");
        }
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    protected EntityManager requireEntityManager() {
        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("El EntityManager no está disponible");
        }
        return em;
    }

    protected <V> V requireArgument(V argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " no puede ser null");
        }
        return argument;
    }

    protected void requirePage(int first, int max) {
        if (first < 0) {
            throw new IllegalArgumentException("first no puede ser negativo");
        }
        if (max < 0) {
            throw new IllegalArgumentException("max no puede ser negativo");
        }
    }

    @Override
    public void crear(T entity) {
        requireArgument(entity, "entity");
        requireEntityManager().persist(entity);
    }

    @Override
    public T modificar(T entity) {
        requireArgument(entity, "entity");
        return requireEntityManager().merge(entity);
    }

    /**
     * Elimina la entidad re-atachándola primero al EntityManager actual si
     * hace falta. Esto evita "Removing a detached instance" cuando el
     * objeto viene desde un bean @ViewScoped (fuera de la transacción que
     * lo cargó), algo común en las pantallas basadas en DefaultModel.
     *
     * <p>El id se obtiene de forma genérica con {@code PersistenceUnitUtil},
     * así que ninguna subclase necesita sobrescribir este método (antes
     * varias lo repetían casi igual).</p>
     */
    @Override
    public void eliminar(T entity) {
        if (entity == null) {
            return;
        }
        EntityManager em = requireEntityManager();
        if (em.contains(entity)) {
            em.remove(entity);
            return;
        }
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        if (id == null) {
            return;
        }
        T managed = em.find(entityClass, id);
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public T findById(ID id) {
        requireArgument(id, "id");
        return requireEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        EntityManager em = requireEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<T> findRange(int start, int max) {
        requirePage(start, max);
        EntityManager em = requireEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em.createQuery(cq)
                 .setFirstResult(start)
                 .setMaxResults(max)
                 .getResultList();
    }

    @Override
    public Long count() {
        EntityManager em = requireEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> rt = cq.from(entityClass);
        cq.select(cb.count(rt));
        return em.createQuery(cq).getSingleResult();
    }

}