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
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    @Override
    public void crear(T entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public T modificar(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public void eliminar(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    @Override
    public T findById(ID id) {
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<T> findRange(int start, int max) {
        EntityManager em = getEntityManager();
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
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> rt = cq.from(entityClass);
        cq.select(cb.count(rt));
        return em.createQuery(cq).getSingleResult();
    }

}
