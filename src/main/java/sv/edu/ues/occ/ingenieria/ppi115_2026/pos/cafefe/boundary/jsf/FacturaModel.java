package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CajaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caja;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class FacturaModel extends DefaultModel<Factura, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacturaRepository facturaRepository;

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    @Inject
    private CajaRepository cajaRepository;

    private Date fechaSeleccionada;
    private Date horaSeleccionada;
    private List<EmpleadoRol> empleadoRoles;
    private List<Caja> cajas;
    private List<ColumnaTabla> columnas;

    @Override
    public FacturaRepository getRepository() {
        return facturaRepository;
    }

    @Override
    public Factura instanciarRegistro() {
        Factura f = new Factura(UUID.randomUUID());
        f.setFechaFacturacion(new Date());
        f.setEstado("PENDIENTE");
        f.setObservaciones("");
        return f;
    }

    @Override
    public Factura getRegistroById(Object id) {
        return id == null ? null : facturaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Factura dato) {
        return dato != null ? dato.getIdFactura() : null;
    }

    @Override
    public void limpiar() {
        super.limpiar();
        Date ahora = new Date();
        this.fechaSeleccionada = ahora;
        this.horaSeleccionada = ahora;
    }

    @Override
    public void seleccionarRegistro(Factura r) {
        super.seleccionarRegistro(r);
        if (r != null && r.getFechaFacturacion() != null) {
            this.fechaSeleccionada = r.getFechaFacturacion();
            this.horaSeleccionada = r.getFechaFacturacion();
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

            this.registro.setFechaFacturacion(combinado.getTime());
        }
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

    public List<String> getEstadosDisponibles() {
        return Arrays.asList("PENDIENTE", "CANCELADO");
    }

    // Carga perezosa para el combo de Empleado-Rol (quien atendió la factura).
    public List<EmpleadoRol> getEmpleadoRoles() {
        if (this.empleadoRoles == null) {
            this.empleadoRoles = empleadoRolRepository != null
                    ? empleadoRolRepository.findAll()
                    : new ArrayList<>();
        }
        return empleadoRoles;
    }

    public void setEmpleadoRoles(List<EmpleadoRol> empleadoRoles) {
        this.empleadoRoles = empleadoRoles;
    }

    // Carga perezosa para el combo de Caja.
    public List<Caja> getCajas() {
        if (this.cajas == null) {
            this.cajas = cajaRepository != null ? cajaRepository.findAll() : new ArrayList<>();
        }
        return cajas;
    }

    public void setCajas(List<Caja> cajas) {
        this.cajas = cajas;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("cliente", "Cliente"),
                    new ColumnaTabla("fechaFacturacion", "Fecha", FORMATO_COLUMNA.DATE),
                    new ColumnaTabla("estado", "Estado"),
                    new ColumnaTabla("idEmpleadoRol.idEmpleado.nombre", "Atendido por"),
                    new ColumnaTabla("idCaja.nombre", "Caja"),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}