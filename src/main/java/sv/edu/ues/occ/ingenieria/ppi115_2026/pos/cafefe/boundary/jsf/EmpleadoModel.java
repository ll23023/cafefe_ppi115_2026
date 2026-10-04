package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 * @author mil
 */
@Named
@ViewScoped
public class EmpleadoModel extends DefaultModel<Empleado, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(EmpleadoModel.class.getName());

    private static final boolean CREAR_ROLES_AL_MODIFICAR = true;

    public static class RolFila implements Serializable {

        private static final long serialVersionUID = 1L;
        private String nombre;
        private boolean existente;

        public RolFila() {
        }

        public RolFila(String nombre) {
            this.nombre = nombre;
        }

        public RolFila(String nombre, boolean existente) {
            this.nombre = nombre;
            this.existente = existente;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public boolean isExistente() {
            return existente;
        }

        public void setExistente(boolean existente) {
            this.existente = existente;
        }
    }

    @Inject
    private EmpleadoRepository empleadoRepository;

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    @Inject
    private RolRepository rolRepository;

    private boolean soloLectura = false;
    private final List<RolFila> filas = new ArrayList<>();
    private List<Rol> roles = new ArrayList<>();
    private List<ColumnaTabla> columnas;

    @Override
    public EmpleadoRepository getRepository() {
        return empleadoRepository;
    }

    @Override
    public Empleado instanciarRegistro() {
        Empleado r = new Empleado(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setComentarios("");
        return r;
    }

    @Override
    public Empleado getRegistroById(Object id) {
        return id == null ? null : empleadoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Empleado dato) {
        return dato != null ? dato.getIdEmpleado() : null;
    }

    @Override
    public void limpiar() {
        super.limpiar();
        this.soloLectura = false;
        this.filas.clear();
        this.cargarRoles();
    }

    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(Empleado e) {
        this.seleccion = e;
        mostrar(e);
        this.soloLectura = false;
        this.estado = ESTADO_CRUD.MODIFICAR;
    }

    public void cancelar() {
            limpiar();
    }

    public void eliminar(Empleado e) {
        if (e == null) {
            return;
        }
        try {
            for (EmpleadoRol er : asignacionesDe(e)) {
                empleadoRolRepository.eliminar(er);
            }
            empleadoRepository.eliminar(e);
            cargarRegistros();
            limpiar();
            info("Empleado eliminado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar empleado", ex);
            error("No se pudo eliminar el empleado: " + ex.getMessage());
        }
    }

    public boolean isPuedeAgregarRoles() {
        return isCreando() || isEditando();
    }

    public void agregarFila() {
        if (!isPuedeAgregarRoles()) {
            return;
        }
        cargarRoles();
        filas.add(new RolFila(""));
    }

    public void quitarFila(RolFila fila) {
        if (!isPuedeAgregarRoles() || fila == null || fila.isExistente()) {
            return;
        }
        filas.remove(fila);
    }

    @Override
    public void btnGuardarHandler() {
        if (soloLectura || registro == null) {
            return;
        }
        boolean esNuevo = isCreando();
        if (!rolesValidos(esNuevo)) {
            return;
        }
        try {
            if (esNuevo) {
                crearConRoles();
            } else {
                modificarConRoles();
            }

            UUID id = registro.getIdEmpleado();
            cargarRegistros();
            Empleado guardado = registros.stream()
                    .filter(e -> id.equals(e.getIdEmpleado()))
                    .findFirst().orElse(null);
            if (guardado != null) {
                this.seleccion = guardado;
                mostrar(guardado);
            } else {
                limpiar();
            }
            cargarRoles();
            info("Empleado guardado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar empleado", ex);
            error("No se pudo guardar el empleado: " + ex.getMessage());
        }
    }

    private void crearConRoles() {
        empleadoRepository.crear(registro);
        Empleado emp = empleadoRepository.findById(registro.getIdEmpleado());
        List<Rol> rolesCreados = new ArrayList<>();
        List<EmpleadoRol> asignacionesCreadas = new ArrayList<>();
        try {
            asignarRolesNuevos(emp, rolesCreados, asignacionesCreadas);
        } catch (RuntimeException ex) {
            deshacer(asignacionesCreadas, rolesCreados);
            empleadoRepository.eliminar(emp);
            throw ex;
        }
    }

    private void modificarConRoles() {
        empleadoRepository.modificar(registro);
        Empleado emp = empleadoRepository.findById(registro.getIdEmpleado());
        List<Rol> rolesCreados = new ArrayList<>();
        List<EmpleadoRol> asignacionesCreadas = new ArrayList<>();
        try {
            asignarRolesNuevos(emp, rolesCreados, asignacionesCreadas);
        } catch (RuntimeException ex) {
            deshacer(asignacionesCreadas, rolesCreados);
            throw ex;
        }
    }

    private void deshacer(List<EmpleadoRol> asignaciones, List<Rol> rolesCreados) {
        for (EmpleadoRol er : asignaciones) {
            empleadoRolRepository.eliminar(er);
        }
        for (Rol r : rolesCreados) {
            rolRepository.eliminar(r);
        }
    }

    private Map<String, String> rolesDeseados() {
        Map<String, String> deseados = new LinkedHashMap<>();
        for (RolFila f : filas) {
            if (f.isExistente()) {
                continue;
            }
            String n = f.getNombre() == null ? "" : f.getNombre().trim();
            if (!n.isEmpty()) {
                deseados.putIfAbsent(clave(n), n);
            }
        }
        return deseados;
    }

    private Map<String, Rol> rolesPorNombre() {
        Map<String, Rol> porNombre = new HashMap<>();
        for (Rol r : rolRepository.findAll()) {
            if (r.getNombre() != null) {
                porNombre.putIfAbsent(clave(r.getNombre()), r);
            }
        }
        return porNombre;
    }

    private Set<String> rolesYaAsignados(Empleado emp) {
        Set<String> asignados = new HashSet<>();
        for (EmpleadoRol er : asignacionesDe(emp)) {
            if (er.getIdRol() != null && er.getIdRol().getNombre() != null) {
                asignados.add(clave(er.getIdRol().getNombre()));
            }
        }
        return asignados;
    }

    private boolean rolesValidos(boolean esNuevo) {
        Map<String, Rol> porNombre = rolesPorNombre();
        Set<String> yaAsignados = esNuevo ? new HashSet<String>() : rolesYaAsignados(registro);
        boolean puedeCrearRoles = esNuevo || CREAR_ROLES_AL_MODIFICAR;
        for (Map.Entry<String, String> d : rolesDeseados().entrySet()) {
            if (yaAsignados.contains(d.getKey())) {
                error("El empleado ya tiene asignado el rol: " + d.getValue());
                return false;
            }
            Rol r = porNombre.get(d.getKey());
            if (r == null) {
                if (!puedeCrearRoles) {
                    error("El rol no existe: " + d.getValue()
                            + ". Al modificar solo puede elegir roles existentes.");
                    return false;
                }
                if (d.getValue().length() > 155) {
                    error("El nombre del rol es demasiado largo (máximo 155 caracteres).");
                    return false;
                }
            } else if (!Boolean.TRUE.equals(r.getActivo())) {
                error("No se puede asignar un rol inactivo: " + r.getNombre());
                return false;
            }
        }
        return true;
    }

    private void asignarRolesNuevos(Empleado emp, List<Rol> rolesCreados,
            List<EmpleadoRol> asignacionesCreadas) {
        Map<String, Rol> porNombre = rolesPorNombre();
        for (Map.Entry<String, String> d : rolesDeseados().entrySet()) {
            Rol rol = porNombre.get(d.getKey());
            if (rol == null) {
                rol = new Rol(UUID.randomUUID());
                rol.setNombre(d.getValue());
                rol.setActivo(Boolean.TRUE);
                rol.setObservaciones("");
                rolRepository.crear(rol);
                rolesCreados.add(rol);
            }
            EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
            er.setIdEmpleado(emp);
            er.setIdRol(rol);
            er.setActivo(Boolean.TRUE);
            er.setObservaciones("");
            empleadoRolRepository.crear(er);
            asignacionesCreadas.add(er);
        }
    }

    private void mostrar(Empleado e) {
        Empleado fresco = empleadoRepository.findById(e.getIdEmpleado());
        this.registro = fresco != null ? fresco : e;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;
        this.filas.clear();
        for (EmpleadoRol er : asignacionesDe(this.registro)) {
            if (er.getIdRol() != null) {
                filas.add(new RolFila(er.getIdRol().getNombre(), true));
            }
        }
    }

    private List<EmpleadoRol> asignacionesDe(Empleado e) {
        if (e == null || e.getIdEmpleado() == null) {
            return new ArrayList<>();
        }
        return empleadoRolRepository.findAll().stream()
                .filter(er -> er.getIdEmpleado() != null
                && e.getIdEmpleado().equals(er.getIdEmpleado().getIdEmpleado()))
                .toList();
    }

    private void cargarRoles() {
        this.roles = rolRepository.findAll().stream()
                .filter(r -> r.getNombre() != null && !r.getNombre().isBlank())
                .sorted((r1, r2) -> String.CASE_INSENSITIVE_ORDER.compare(r1.getNombre(), r2.getNombre()))
                .toList();
    }

    private static String clave(String nombre) {
        return nombre == null ? null : nombre.trim().toLowerCase(Locale.ROOT);
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
            return "Detalle del empleado";
        }
        return isEditando() ? "Editar empleado" : "Nuevo empleado";
    }

    public List<RolFila> getFilas() {
        return filas;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombres"),
                    new ColumnaTabla("apellido", "Apellidos"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("comentarios", "Comentarios"));
        }
        return columnas;
    }
}