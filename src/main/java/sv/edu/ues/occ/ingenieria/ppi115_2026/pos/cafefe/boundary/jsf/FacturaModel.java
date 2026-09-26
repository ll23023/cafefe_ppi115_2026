package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
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
    private Date fechaSeleccionada;
    private Date horaSeleccionada;

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
        if (id != null && this.registros != null && !this.registros.isEmpty()) {
            UUID busca = (UUID) id;
            return this.registros.stream()
                    .filter(f -> f.getIdFactura().equals(busca))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Object getIdByRegistro(Factura dato) {
        if (dato != null) {
            return dato.getIdFactura();
        }
        return null;
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
        return Arrays.asList("Pendiente", "Cancelado");
    }

}
