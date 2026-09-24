package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;

/**
 * Clase abstracta base para los Model (Boundary JSF).
 *
 * @param <T> Entidad gestionada
 * @param <ID> Tipo del ID/PK de la Entidad (ej: UUID)

 */
public abstract class DefaultModel<T,ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<T> registros;
    protected T registro;
    protected ESTADO_CRUD estado = ESTADO_CRUD.NINGUNO;

    // Métodos abstractos que implementa cada modelo hijo
    public abstract T instanciarRegistro();
    public abstract T getRegistroById(Object id);
    public abstract Object getIdByRegistro(T dato);

    // Conexión genérica con tu DefaultDAO
    public abstract DefaultRepository<T, ID> getDao();

    @PostConstruct
    public void init() {
        this.cargarRegistros();
        this.limpiar();
    }

    public void cargarRegistros() {
        if (getDao() != null) {
            this.registros = getDao().findAll();
        } else {
            this.registros = new ArrayList<>();
        }
    }

    public void limpiar() {
        this.registro = this.instanciarRegistro();
        this.estado = ESTADO_CRUD.CREAR;
    }
   public void btnNuevoHandler() {
        this.limpiar();
    }

    public void btnGuardarHandler() {
        if (this.registro != null && getDao() != null) {
            getDao().crear(this.registro);
            this.cargarRegistros();
            this.limpiar();
        }
    }

    public void btnModificarHandler() {
        if (this.registro != null && getDao() != null) {
            getDao().modificar(this.registro);
            this.cargarRegistros();
            this.limpiar();
        }
    }

    public void btnEliminarHandler() {
        if (this.registro != null && getDao() != null) {
            getDao().eliminar(this.registro);
            this.cargarRegistros();
            this.limpiar();
        }
    }

    public void seleccionarRegistro(T r) {
        this.registro = r;
        this.estado = ESTADO_CRUD.MODIFICAR;
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

    public ESTADO_CRUD getEstado() {
        return estado;
    }

    public void setEstado(ESTADO_CRUD estado) {
        this.estado = estado;
    }
}
