package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Orden;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class OrdenModel extends DefaultModel<Orden, UUID> {

    private static final long serialVersionUID = 1L;

    private Date fechaSeleccionada;
    private Date horaSeleccionada;

    @Inject
    private OrdenRepository ordenRepository;

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    private List<EmpleadoRol> empleadoRoles;
    private List<ColumnaTabla> columnas;

    @Override
    public OrdenRepository getRepository() {
        return ordenRepository;
    }

    @Override
    public Orden instanciarRegistro() {
        Orden r = new Orden(UUID.randomUUID());
        r.setFechaCreacion(new Date());
        return r;
    }

    @Override
    public Orden getRegistroById(Object id) {
        return id == null ? null : ordenRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Orden dato) {
        return dato != null ? dato.getIdOrden() : null;
    }

    @Override
    public void limpiar() {
        super.limpiar();
        Date ahora = new Date();
        this.fechaSeleccionada = ahora;
        this.horaSeleccionada = ahora;
    }

    @Override
    public void seleccionarRegistro(Orden r) {
        super.seleccionarRegistro(r);
        if (r != null && r.getFechaCreacion() != null) {
            this.fechaSeleccionada = r.getFechaCreacion();
            this.horaSeleccionada = r.getFechaCreacion();
        }
    }

    @Override
    public void btnGuardarHandler() {
        combinarFechaHora();
        super.btnGuardarHandler();
    }

    private void combinarFechaHora() {
        if (this.registro != null) {
            Calendar calFecha = Calendar.getInstance();
            Calendar calHora = Calendar.getInstance();

            if (this.fechaSeleccionada != null) {
                calFecha.setTime(this.fechaSeleccionada);
            }
            if (this.horaSeleccionada != null) {
                calHora.setTime(this.horaSeleccionada);
            }

            Calendar combinado = Calendar.getInstance();
            combinado.set(Calendar.YEAR, calFecha.get(Calendar.YEAR));
            combinado.set(Calendar.MONTH, calFecha.get(Calendar.MONTH));
            combinado.set(Calendar.DAY_OF_MONTH, calFecha.get(Calendar.DAY_OF_MONTH));
            combinado.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
            combinado.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
            combinado.set(Calendar.SECOND, 0);
            combinado.set(Calendar.MILLISECOND, 0);
            this.registro.setFechaCreacion(combinado.getTime());
        }
    }

    // Carga perezosa: solo consulta la BD la primera vez que el XHTML la pide
    public List<EmpleadoRol> getEmpleadoRoles() {
        if (this.empleadoRoles == null) {
            if (empleadoRolRepository != null) {
                this.empleadoRoles = empleadoRolRepository.findAll();
            } else {
                this.empleadoRoles = new ArrayList<>();
            }
        }
        return empleadoRoles;
    }

    public void setEmpleadoRoles(List<EmpleadoRol> empleadoRoles) {
        this.empleadoRoles = empleadoRoles;
    }

    public Date getFechaSeleccionada() {
        return fechaSeleccionada;
    }

    public void setFechaSeleccionada(Date fechaSeleccionada) {
        this.fechaSeleccionada = fechaSeleccionada;
    }

    public Date getHoraSeleccionada() {
        return horaSeleccionada;
    }

    public void setHoraSeleccionada(Date horaSeleccionada) {
        this.horaSeleccionada = horaSeleccionada;
    }

    /**
     * Columnas para tablaDinamica; faltaba este método.
     */
    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("idEmpleadoRol.idEmpleado.nombre", "Empleado"),
                    new ColumnaTabla("idEmpleadoRol.idEmpleado.apellido", "Apellido"),
                    new ColumnaTabla("idEmpleadoRol.idRol.nombre", "Rol"),
                    new ColumnaTabla("fechaCreacion", "Fecha", FORMATO_COLUMNA.DATE)
            );
        }
        return columnas;
    }
}