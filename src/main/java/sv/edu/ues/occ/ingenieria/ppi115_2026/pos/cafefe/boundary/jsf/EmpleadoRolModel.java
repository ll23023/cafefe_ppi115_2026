package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
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
    
    /**
     * Así es como se definen las columnas de la tabla dinámica. 
     * La propiedad puede ser una ruta anidada, como {@code idEmpleado.nombre}.
     */
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
        if (id != null && this.registros != null && !this.registros.isEmpty()) {
            UUID busca = (UUID) id;
            return this.registros.stream()
                    .filter(r -> r.getIdEmpleadoRol().equals(busca))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Object getIdByRegistro(EmpleadoRol dato) {
        if (dato != null) {
            return dato.getIdEmpleadoRol();
        }
        return null;
    }

    // Getters y Setters para los combos
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
