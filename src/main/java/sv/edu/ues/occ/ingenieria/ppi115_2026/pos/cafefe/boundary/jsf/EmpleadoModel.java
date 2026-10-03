package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public static class RolFila implements Serializable {

        private static final long serialVersionUID = 1L;
        private String nombre;

        public RolFila() {
        }

        public RolFila(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
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
        if (estado == ESTADO_CRUD.MODIFICAR && seleccion != null) {
            mostrar(seleccion);
        } else {
            limpiar();
        }
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

    // Los roles solo se pueden agregar/quitar al crear (también se protege en servidor).
    public void agregarFila() {
        if (!isCreando()) {
            return;
        }
        cargarRoles();
        filas.add(new RolFila(""));
    }

    public void quitarFila(RolFila fila) {
        if (!isCreando()) {
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
        // Los roles solo se validan al crear: al modificar no se tocan.
        if (esNuevo && !rolesValidos()) {
            return;
        }
        try {
            if (esNuevo) {
                crearConRoles();
            } else {
                empleadoRepository.modificar(registro);
            }

            // Refrescar: el empleado guardado queda en solo lectura
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

    /**
     * Crea el empleado y sus roles (creando también los roles que no existían).
     * Si algo falla se deshace todo lo creado en esta operación: asignaciones,
     * empleado y roles nuevos, para no dejar datos a medias.
     */
    private void crearConRoles() {
        empleadoRepository.crear(registro);
        Empleado emp = empleadoRepository.findById(registro.getIdEmpleado());
        List<Rol> rolesCreados = new ArrayList<>();
        try {
            asignarRoles(emp, rolesCreados);
        } catch (RuntimeException ex) {
            for (EmpleadoRol er : asignacionesDe(emp)) {
                empleadoRolRepository.eliminar(er);
            }
            empleadoRepository.eliminar(emp);
            for (Rol r : rolesCreados) {
                rolRepository.eliminar(r);
            }
            throw ex;
        }
    }

    // Nombres de rol de la tabla: sin vacíos ni repetidos (sin distinguir mayúsculas).
    private Map<String, String> rolesDeseados() {
        Map<String, String> deseados = new LinkedHashMap<>();
        for (RolFila f : filas) {
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

    // Un rol existente debe estar activo. Un nombre que no existe es un rol
    // nuevo: se crea (activo) al guardar el empleado. Solo ocurre al crear.
    private boolean rolesValidos() {
        Map<String, Rol> porNombre = rolesPorNombre();
        for (Map.Entry<String, String> d : rolesDeseados().entrySet()) {
            Rol r = porNombre.get(d.getKey());
            if (r == null) {
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

    private void asignarRoles(Empleado emp, List<Rol> rolesCreados) {
        Map<String, Rol> porNombre = rolesPorNombre();
        for (Map.Entry<String, String> d : rolesDeseados().entrySet()) {
            Rol rol = porNombre.get(d.getKey());
            if (rol == null) {
                // Rol nuevo escrito en el combo: se crea activo
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
                filas.add(new RolFila(er.getIdRol().getNombre()));
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

    // Carga todos los roles (activos e inactivos); la vista deshabilita los inactivos.
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