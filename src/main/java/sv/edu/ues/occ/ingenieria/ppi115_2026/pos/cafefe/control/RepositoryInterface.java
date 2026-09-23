package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author mikelb
 */
public interface RepositoryInterface<T> {
    void crear(T registro);

    List<T> findAll();

    T findById(UUID id);

    List<T> getRange(int first, int last);

    Long count();

    T modificar(T registro);

    void eliminar(T registro);
}
