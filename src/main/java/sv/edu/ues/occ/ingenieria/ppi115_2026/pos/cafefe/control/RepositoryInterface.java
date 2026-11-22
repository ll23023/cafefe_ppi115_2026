package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import java.util.List;

/**
 * Contrato común para las operaciones de persistencia.
 *
 * <p>Las implementaciones rechazan argumentos obligatorios nulos y rangos de
 * paginación inválidos con {@link IllegalArgumentException}. Si no pueden
 * acceder a la unidad de persistencia, lanzan {@link IllegalStateException}.</p>
 *
 * @author mikelb
 */
public interface RepositoryInterface<T, ID> {
    /**
     * Persiste un registro.
     *
     * @throws IllegalArgumentException si el registro es nulo
     * @throws IllegalStateException si no hay un EntityManager disponible para persistir el registro
     */
    void crear(T registro);

    /**
     * Recupera todos los registros.
     *
     * @throws IllegalStateException si la persistencia no está disponible
     */
    List<T> findAll();

    /**
     * Busca un registro por su identificador.
     *
     * @throws IllegalArgumentException si el identificador es nulo
     * @throws IllegalStateException si la persistencia no está disponible
     */
    T findById(ID id);

    /**
     * Recupera un rango de registros.
     *
     * @throws IllegalArgumentException si el inicio o el máximo son negativos
     * @throws IllegalStateException si la persistencia no está disponible
     */
    List<T> findRange(int first, int last);

    /**
     * Cuenta los registros.
     *
     * @throws IllegalStateException si la persistencia no está disponible
     */
    Long count();

    /**
     * Actualiza un registro.
     *
     * @throws IllegalArgumentException si el registro es nulo
     * @throws IllegalStateException si la persistencia no está disponible
     */
    T modificar(T registro);

    /**
     * Elimina un registro. Un registro nulo se ignora.
     *
     * @throws IllegalStateException si el registro no es nulo y la persistencia no está disponible
     */
    void eliminar(T registro);
}
