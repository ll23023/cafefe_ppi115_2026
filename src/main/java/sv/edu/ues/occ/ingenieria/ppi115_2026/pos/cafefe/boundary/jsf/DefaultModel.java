package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;

/**
 *
 * @author mil
 * 
 * @param <T> tipo de entidad administrada por la pantalla
 * @param <ID> tipo del identificador de la entidad
 */
public abstract class DefaultModel<T, ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<T> registros;
    protected T registro;
    protected T seleccion;
    protected ESTADO_CRUD estado = ESTADO_CRUD.NINGUNO;

    /**
     * Crea la instancia inicial que utilizará el formulario de alta.
     *
     * @return entidad nueva con los valores predeterminados de la pantalla
     */
    public abstract T instanciarRegistro();

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador recibido por la vista o por otra operación CRUD
     * @return entidad encontrada o {@code null} si no existe
     */
    public abstract T getRegistroById(Object id);

    /**
     * Extrae el identificador de una entidad.
     *
     * @param dato entidad de la que se obtendrá el identificador
     * @return identificador de {@code dato}, o {@code null} si {@code dato} es
     * null
     */
    public abstract Object getIdByRegistro(T dato);

    /**
     * Proporciona el repositorio utilizado por las operaciones CRUD comunes.
     *
     * @return repositorio de la entidad administrada
     */
    public abstract DefaultRepository<T, ID> getRepository();

    /**
     * Carga los registros iniciales y prepara el formulario en modo creación.
     */
    @PostConstruct
    public void init() {
        this.cargarRegistros();
        this.limpiar();
    }

    /**
     * Recarga la lista desde el repositorio. Si el repositorio aún no está
     * disponible, deja una lista vacía para evitar errores durante la vista.
     */
    public void cargarRegistros() {
        if (getRepository() != null) {
            this.registros = getRepository().findAll();
        } else {
            this.registros = new ArrayList<>();
        }
    }

    /**
     * Reinicia el formulario y la selección de la tabla.
     *
     * <p>
     * La entidad concreta decide sus valores iniciales mediante
     * {@link #instanciarRegistro()}. Por ejemplo, puede establecer un estado
     * activo por defecto o generar un UUID antes de mostrar el formulario.
     * </p>
     */
    public void limpiar() {
        this.registro = this.instanciarRegistro();
        this.seleccion = null;
        this.estado = ESTADO_CRUD.CREAR;
    }

    /**
     * Activa el flujo de creación y descarta cualquier edición o selección
     * previa.
     */
    public void btnNuevoHandler() {
        this.limpiar();
    }

    /**
     * Persiste el registro actual como nuevo o como modificación según
     * {@link #estado}; después actualiza la tabla y reinicia el formulario.
     */
    public void btnGuardarHandler() {
        // El estado decide si el mismo botón crea un registro o guarda una edición.
        if (this.registro != null && getRepository() != null) {
            if (this.estado == ESTADO_CRUD.MODIFICAR) {
                getRepository().modificar(this.registro);
            } else {
                getRepository().crear(this.registro);
            }
            this.cargarRegistros();
            this.limpiar();
        }
    }

    /**
     * Mantiene el método de modificación directa para páginas antiguas que aún
     * lo utilizan; las páginas nuevas pueden usar el flujo unificado de
     * guardar.
     */
    public void btnModificarHandler() {
        if (this.registro != null && getRepository() != null) {
            getRepository().modificar(this.registro);
            this.cargarRegistros();
            this.limpiar();
        }
    }

    /**
     * Elimina el registro actual, recarga la tabla y limpia la selección.
     */
    public void btnEliminarHandler() {
        if (this.registro != null && getRepository() != null) {
            getRepository().eliminar(this.registro);
            this.cargarRegistros();
            this.limpiar();
        }
    }

    /**
     * Carga una fila seleccionada en el formulario y cambia el modelo a
     * edición.
     *
     * @param r entidad seleccionada en la tabla; {@code null} no modifica el
     * estado
     */
    public void seleccionarRegistro(T r) {
        if (r != null) {
            this.registro = r;
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    /**
     * Copia la selección actual al formulario si existe una fila seleccionada.
     */
    public void btnEditarHandler() {
        this.seleccionarRegistro(this.seleccion);
    }

    /**
     * Indica si el formulario está editando un registro existente.
     *
     * @return {@code true} cuando el estado actual es {@code MODIFICAR}
     */
    public boolean isEditando() {
        return this.estado == ESTADO_CRUD.MODIFICAR;
    }

    /**
     * Indica si el formulario está en modo creación (el único modo en que se
     * permiten cambios en listas de selección y ventanas emergentes).
     * @return <code>true</code> si se está creando un nuevo registro; <code>false</code> si no.
     */
    public boolean isCreando() {
        return this.estado == ESTADO_CRUD.CREAR;
    }

    // Getters y Setters
    public List<T> getRegistros() {
        return registros;
    }

    public void setRegistros(List<T> registros) {
        this.registros = registros;
    }

    public T getRegistro() {
        return registro;
    }

    public void setRegistro(T registro) {
        this.registro = registro;
    }

    public T getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(T seleccion) {
        this.seleccion = seleccion;
    }

    public ESTADO_CRUD getEstado() {
        return estado;
    }

    public void setEstado(ESTADO_CRUD estado) {
        this.estado = estado;
    }
}
