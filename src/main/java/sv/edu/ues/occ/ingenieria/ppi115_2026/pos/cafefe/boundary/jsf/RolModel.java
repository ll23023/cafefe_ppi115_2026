package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class RolModel extends DefaultModel<Rol, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(RolModel.class.getName());

    @Inject
    private RolRepository rolRepository;

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    private boolean soloLectura = false;
    private List<Empleado> empleadosDelRol = new ArrayList<>();
    private List<ColumnaTabla> columnas;

    @Override
    public RolRepository getRepository() {
        return rolRepository;
    }

    @Override
    public Rol instanciarRegistro() {
        Rol r = new Rol(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        return r;
    }

    @Override
    public Rol getRegistroById(Object id) {
        return id == null ? null : rolRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Rol dato) {
        return dato != null ? dato.getIdRol() : null;
    }

    @Override
    public void limpiar() {
        super.limpiar();
        this.soloLectura = false;
        this.empleadosDelRol = new ArrayList<>();
    }

    // Listener del clic en una fila: muestra el rol y sus empleados.
    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(Rol r) {
        this.seleccion = r;
        mostrar(r);
        this.soloLectura = false;
        this.estado = ESTADO_CRUD.MODIFICAR;
    }

    public void cancelar() {
        if (estado == ESTADO_CRUD.MODIFICAR && seleccion != null) {
            mostrar(seleccion);
        } else {
            limpiar();
        }
    }

    // No deja borrar un rol que tenga empleados.
    public void eliminar(Rol r) {
        if (r == null) {
            return;
        }
        try {
            int asignados = empleadosConRol(r).size();
            if (asignados > 0) {
                error("No se puede eliminar \"" + r.getNombre() + "\": lo tienen " + asignados
                        + " empleado(s). Puede marcarlo como inactivo en su lugar.");
                return;
            }
            rolRepository.eliminar(r);
            cargarRegistros();
            limpiar();
            info("Rol eliminado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar rol", ex);
            error("No se pudo eliminar el rol: " + ex.getMessage());
        }
    }

    @Override
    public void btnGuardarHandler() {
        if (soloLectura || registro == null) {
            return;
        }
        final String nombre = registro.getNombre() == null ? "" : registro.getNombre().trim();
        registro.setNombre(nombre);

        // El nombre no se puede repetir: en empleados los roles se buscan por nombre
        boolean repetido = rolRepository.findAll().stream()
                .anyMatch(o -> o.getNombre() != null
                && o.getNombre().trim().equalsIgnoreCase(nombre)
                && !o.getIdRol().equals(registro.getIdRol()));
        if (repetido) {
            error("Ya existe un rol llamado \"" + nombre + "\".");
            return;
        }

        try {
            if (estado == ESTADO_CRUD.MODIFICAR) {
                rolRepository.modificar(registro);
            } else {
                rolRepository.crear(registro);
            }
            cargarRegistros();
            limpiar();
            info("Rol guardado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar rol", ex);
            error("No se pudo guardar el rol: " + ex.getMessage());
        }
    }

    private void mostrar(Rol r) {
        Rol fresco = rolRepository.findById(r.getIdRol());
        this.registro = fresco != null ? fresco : r;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;
        this.empleadosDelRol = empleadosConRol(this.registro);
    }

    // Empleados que tienen asignado el rol, sin repetidos y ordenados por nombre.
    private List<Empleado> empleadosConRol(Rol r) {
        return empleadoRolRepository.findAll().stream()
                .filter(er -> er.getIdRol() != null && er.getIdEmpleado() != null
                && r.getIdRol().equals(er.getIdRol().getIdRol()))
                .map(EmpleadoRol::getIdEmpleado)
                .distinct()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getNombre() == null ? "" : a.getNombre(),
                b.getNombre() == null ? "" : b.getNombre()))
                .toList();
    }

    private void info(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public boolean isSoloLectura() {
        return soloLectura;
    }

    public String getTituloFormulario() {
        if (soloLectura) {
            return "Detalle del rol";
        }
        return isEditando() ? "Editar rol" : "Nuevo rol";
    }

    public List<Empleado> getEmpleadosDelRol() {
        return empleadosDelRol;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("observaciones", "Observaciones"));
        }
        return columnas;
    }
}