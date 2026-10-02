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

    public void agregarFila() {
        cargarRoles();
        filas.add(new RolFila(""));
        LOG.log(Level.WARNING, "[DIAG] agregarFila: filas={0}, roles disponibles={1}",
                new Object[]{filas.size(), roles.size()});
    }

    public void quitarFila(RolFila fila) {
        filas.remove(fila);
    }

    @Override
    public void btnGuardarHandler() {
        if (soloLectura || registro == null) {
            return;
        }
        if (!rolesValidos()) {
            return;
        }
        try {
            boolean esNuevo = estado != ESTADO_CRUD.MODIFICAR;
            if (esNuevo) {
                empleadoRepository.crear(registro);
            } else {
                empleadoRepository.modificar(registro);
            }
            Empleado emp = empleadoRepository.findById(registro.getIdEmpleado());
            sincronizarRoles(emp);

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
        limpiar();
    }

    public void guardarRoles() {
        if (seleccion == null || registro == null || estado == ESTADO_CRUD.CREAR) {
            error("Seleccione un empleado guardado para asignarle roles.");
            return;
        }
        if (!rolesValidos()) {
            return;
        }
        try {
            Empleado emp = empleadoRepository.findById(registro.getIdEmpleado());
            sincronizarRoles(emp);
            mostrar(seleccion);
            cargarRoles();
            info("Roles guardados.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar roles", ex);
            error("No se pudieron guardar los roles: " + ex.getMessage());
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

    // No se puede asignar un rol existente pero inactivo. 
    private boolean rolesValidos() {
        Map<String, String> deseados = rolesDeseados();
        LOG.log(Level.WARNING, "[DIAG] filas recibidas={0}, roles a asignar={1}",
                new Object[]{filas.stream().map(RolFila::getNombre).toList(), deseados.values()});
        Map<String, Rol> porNombre = rolesPorNombre();
        for (String k : deseados.keySet()) {
            Rol r = porNombre.get(k);
            if (r != null && !Boolean.TRUE.equals(r.getActivo())) {
                error("No se puede asignar un rol inactivo: " + r.getNombre());
                return false;
            }
        }
        return true;
    }

    /**
     * Deja las asignaciones del empleado igual a la tabla de roles: quita las
     * que ya no están, agrega las nuevas y crea el rol si no existía.
     */
    private void sincronizarRoles(Empleado emp) {
        Map<String, String> deseados = rolesDeseados();
        Map<String, Rol> porNombre = rolesPorNombre();

        Set<String> yaAsignados = new HashSet<>();
        for (EmpleadoRol er : asignacionesDe(emp)) {
            String k = er.getIdRol() != null ? clave(er.getIdRol().getNombre()) : null;
            if (k == null || !deseados.containsKey(k)) {
                empleadoRolRepository.eliminar(er);
            } else {
                yaAsignados.add(k);
            }
        }

        for (Map.Entry<String, String> d : deseados.entrySet()) {
            if (yaAsignados.contains(d.getKey())) {
                continue;
            }
            Rol rol = porNombre.get(d.getKey());
            if (rol == null) {
                rol = new Rol(UUID.randomUUID());
                rol.setNombre(d.getValue());
                rol.setActivo(Boolean.TRUE);
                rolRepository.crear(rol);
                porNombre.put(d.getKey(), rol);
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
        LOG.log(Level.WARNING, "[DIAG] cargarRoles: {0} roles", roles.size());
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