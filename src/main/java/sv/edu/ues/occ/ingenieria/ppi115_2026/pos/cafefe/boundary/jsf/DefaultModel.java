package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;

/**
 *
 * @author mil
 *
 * Clase abstracta base para los Model (Boundary JSF).
 *
 * @param <T> Entidad gestionada
 * @param <ID> Tipo del ID
 *
 */
public abstract class DefaultModel<T, ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<T> registros;
    protected T registro;
    protected T seleccion;
    protected ESTADO_CRUD estado = ESTADO_CRUD.NINGUNO;

    public abstract T instanciarRegistro();

    public abstract T getRegistroById(Object id);

    public abstract Object getIdByRegistro(T dato);

    public abstract DefaultRepository<T, ID> getRepository();

    @PostConstruct
    public void init() {
        this.cargarRegistros();
        this.limpiar();
    }

    public void cargarRegistros() {
        if (getRepository() != null) {
            this.registros = getRepository().findAll();
        } else {
            this.registros = new ArrayList<>();
        }
    }

    public void limpiar() {
        this.registro = this.instanciarRegistro();
        this.seleccion = null;
        this.estado = ESTADO_CRUD.CREAR;
    }

    public void btnNuevoHandler() {
        this.limpiar();
    }

    public void btnGuardarHandler() {
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

    public void btnModificarHandler() {
        if (this.registro != null && getRepository() != null) {
            getRepository().modificar(this.registro);
            this.cargarRegistros();
            this.limpiar();
        }
    }

    public void btnEliminarHandler() {
        if (this.registro != null && getRepository() != null) {
            getRepository().eliminar(this.registro);
            this.cargarRegistros();
            this.limpiar();
        }
    }

    public void seleccionarRegistro(T r) {
        if (r != null) {
            this.registro = r;
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    public void btnEditarHandler() {
        this.seleccionarRegistro(this.seleccion);
    }

    public boolean isEditando() {
        return this.estado == ESTADO_CRUD.MODIFICAR;
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
