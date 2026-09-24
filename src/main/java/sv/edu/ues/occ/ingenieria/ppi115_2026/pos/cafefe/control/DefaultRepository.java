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
    
    public  Class<T> defaultClass;
    
    public DefaultRepository(Class<T> defaultClass) {
        this.defaultClass = defaultClass;
    }
    
    public abstract EntityManager getEntityManager();
    
    @Override
    public void crear(T registro) {
        getEntityManager().persist(registro);
    }

    @Override
    public List<T> findAll() {
        List<T> result = this.getEntityManager().createQuery(buildQuery()).getResultList();
        return result;
    }

    public T findById(ID id) {
        return this.getEntityManager().find(this.defaultClass, id);
    }

    @Override
    public List<T> getRange(int first, int last) {
        List<T> result = this.getEntityManager().createQuery(buildQuery()).setFirstResult(first).setMaxResults(last).getResultList();
        return result;
    }

    @Override
    public Long count() {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<T> root = cr.from(this.defaultClass);

        cr.select(cb.count(root));
        
        Long result = this.getEntityManager().createQuery(cr).getSingleResult();
        return result;
    }

    @Override
    public T modificar(T registro) {
        return this.getEntityManager().merge(registro);
    }

    @Override
    public void eliminar(T registro) {
        this.getEntityManager().remove(registro);
    }

    private CriteriaQuery<T> buildQuery() {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(this.defaultClass);
        Root<T>  root = cr.from(this.defaultClass);
        
        cr.select(root);
        
        return cr;
    }
}
