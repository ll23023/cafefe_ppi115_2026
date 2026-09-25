package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import java.util.List;

/**
 *
 * @author mikelb
 */
public interface RepositoryInterface<T, ID> {
    void crear(T registro);

    List<T> findAll();

    T findById(ID id);

    List<T> findRange(int first, int last);

    Long count();

    T modificar(T registro);

    void eliminar(T registro);
}
