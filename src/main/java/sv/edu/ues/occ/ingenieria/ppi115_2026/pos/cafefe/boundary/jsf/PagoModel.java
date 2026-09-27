package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Pago;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class PagoModel extends DefaultModel<Pago, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PagoRepository pagoRepository;

    @Inject
    private FacturaRepository facturaRepository;

    private List<Factura> facturas;
    private List<ColumnaTabla> columnas;

    @Override
    public PagoRepository getRepository() {
        return pagoRepository;
    }

    @Override
    public Pago instanciarRegistro() {
        Pago p = new Pago(UUID.randomUUID());
        p.setEstado("PENDIENTE");
        p.setObservaciones("");
        return p;
    }

    @Override
    public Pago getRegistroById(Object id) {
        return id == null ? null : pagoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Pago dato) {
        return dato != null ? dato.getIdPago() : null;
    }

    /**
     * Ajustá estos valores si tu negocio maneja otros estados de pago.
     */
    public List<String> getEstadosDisponibles() {
        return Arrays.asList("PENDIENTE", "COMPLETADO", "CANCELADO");
    }

    // Carga perezosa para el combo de Factura.
    public List<Factura> getFacturas() {
        if (this.facturas == null) {
            this.facturas = facturaRepository != null ? facturaRepository.findAll() : new ArrayList<>();
        }
        return facturas;
    }

    public void setFacturas(List<Factura> facturas) {
        this.facturas = facturas;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("idFactura.cliente", "Factura de"),
                    new ColumnaTabla("estado", "Estado"),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}