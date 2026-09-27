package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoDetalleRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Pago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.PagoDetalle;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class PagoDetalleModel extends DefaultModel<PagoDetalle, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PagoDetalleRepository pagoDetalleRepository;

    @Inject
    private PagoRepository pagoRepository;

    private List<Pago> pagos;
    private List<ColumnaTabla> columnas;

    @Override
    public PagoDetalleRepository getRepository() {
        return pagoDetalleRepository;
    }

    @Override
    public PagoDetalle instanciarRegistro() {
        PagoDetalle p = new PagoDetalle(UUID.randomUUID());
        p.setObservaciones("");
        p.setReferenciaExterna("");
        return p;
    }

    @Override
    public PagoDetalle getRegistroById(Object id) {
        return id == null ? null : pagoDetalleRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(PagoDetalle dato) {
        return dato != null ? dato.getIdPagoDetalle() : null;
    }

    /**
     * Ajustá estos valores si tu negocio maneja otros tipos de pago.
     */
    public List<String> getTiposPagoDisponibles() {
        return Arrays.asList("EFECTIVO", "TARJETA", "TRANSFERENCIA");
    }

    // Carga perezosa para el combo de Pago.
    public List<Pago> getPagos() {
        if (this.pagos == null) {
            this.pagos = pagoRepository != null ? pagoRepository.findAll() : new ArrayList<>();
        }
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("idPago.idFactura.cliente", "Factura de"),
                    new ColumnaTabla("monto", "Monto", FORMATO_COLUMNA.CURRENCY),
                    new ColumnaTabla("tipoPago", "Tipo de pago"),
                    new ColumnaTabla("referenciaExterna", "Referencia"),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}