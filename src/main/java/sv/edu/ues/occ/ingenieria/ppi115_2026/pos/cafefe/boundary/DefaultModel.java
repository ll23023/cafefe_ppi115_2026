package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RepositoryInterface;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Clase abstracta base para los Model (Boundary JSF).
 *
 * @param <T> Entidad gestionada
 */
public abstract class DefaultModel<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected T registro;
    protected UUID idRegistro;
    protected List<T> lista;

    protected abstract RepositoryInterface<T> getDAO();

    @PostConstruct
    public void init() {
        instanciarRegistro();
        cargarLista();
    }

    public void instanciarRegistro() {
        try {
            this.registro = nuevaInstancia();
            this.idRegistro = null;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo instanciar la entidad", e);
        }
    }

    protected abstract T nuevaInstancia();

    public void guardar() {
        try {
            getDAO().crear(registro);
            addMessage(FacesMessage.SEVERITY_INFO, "Registro creado", "Operación exitosa");
            instanciarRegistro();
            cargarLista();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al crear", e.getMessage());
        }
    }

    public void actualizar() {
        try {
            getDAO().modificar(registro);
            addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "Operación exitosa");
            instanciarRegistro();
            cargarLista();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar", e.getMessage());
        }
    }

    public void eliminar() {
        try {
            getDAO().eliminar(registro);
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "Operación exitosa");
            instanciarRegistro();
            cargarLista();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar", e.getMessage());
        }
    }

    public void cargarLista() {
        this.lista = getDAO().findAll();
    }

    public void setIdRegistro(UUID id) {
        this.idRegistro = id;
        if (idValido(id)) {
            T encontrado = getDAO().findById(id);
            if (encontrado != null) {
                this.registro = encontrado;
            }
        }
    }

    public UUID getIdRegistro() {
        return idRegistro;
    }

    public T getRegistro() {
        return registro;
    }

    public void setRegistro(T registro) {
        this.registro = registro;
    }

    public List<T> getLista() {
        return lista;
    }

    public void setLista(List<T> lista) {
        this.lista = lista;
    }

    protected boolean idValido(UUID id) {
        return id != null;
    }

    protected void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
}