package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
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
public class EmpleadoRolModel extends DefaultModel<EmpleadoRol, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    @Inject
    private EmpleadoRepository empleadoRepository;

    @Inject
    private RolRepository rolRepository;

    private List<Empleado> empleados;
    private List<Rol> roles;

    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("idEmpleadoRol", "ID"),
            new ColumnaTabla("idEmpleado.nombre", "Empleado"),
            new ColumnaTabla("idEmpleado.apellido", "Apellido"),
            new ColumnaTabla("idRol.nombre", "Rol"),
            new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    @Override
    public EmpleadoRolRepository getRepository() {
        return empleadoRolRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        this.cargarEmpleados();
        this.cargarRoles();
    }

    public void cargarEmpleados() {
        if (empleadoRepository != null) {
            this.empleados = empleadoRepository.findAll();
        } else {
            this.empleados = new ArrayList<>();
        }
    }

    public void cargarRoles() {
        if (rolRepository != null) {
            // Todos los roles: la vista deshabilita los inactivos (itemDisabled)
            this.roles = rolRepository.findAll();
        } else {
            this.roles = new ArrayList<>();
        }
    }

    @Override
    public EmpleadoRol instanciarRegistro() {
        EmpleadoRol r = new EmpleadoRol(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        return r;
    }

    @Override
    public EmpleadoRol getRegistroById(Object id) {
        return id == null ? null : empleadoRolRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(EmpleadoRol dato) {
        return dato != null ? dato.getIdEmpleadoRol() : null;
    }

    @Override
    public void btnGuardarHandler() {
        if (this.registro == null) {
            return;
        }
        if (isEditando()) {
            // Empleado y rol no cambian al modificar: se restauran desde la BD
            EmpleadoRol original = empleadoRolRepository.findById(registro.getIdEmpleadoRol());
            if (original != null) {
                registro.setIdEmpleado(original.getIdEmpleado());
                registro.setIdRol(original.getIdRol());
            }
        } else {
            if (registro.getIdRol() != null
                    && !Boolean.TRUE.equals(registro.getIdRol().getActivo())) {
                error("No se puede asignar un rol inactivo.");
                return;
            }
            if (existeAsignacion(registro)) {
                error("Ese empleado ya tiene asignado ese rol.");
                return;
            }
        }
        super.btnGuardarHandler();
    }

    private boolean existeAsignacion(EmpleadoRol nuevo) {
        if (nuevo.getIdEmpleado() == null || nuevo.getIdRol() == null) {
            return false;
        }
        return empleadoRolRepository.findAll().stream().anyMatch(er
                -> er.getIdEmpleado() != null && er.getIdRol() != null
                && er.getIdEmpleado().getIdEmpleado().equals(nuevo.getIdEmpleado().getIdEmpleado())
                && er.getIdRol().getIdRol().equals(nuevo.getIdRol().getIdRol()));
    }

    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public List<Empleado> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}