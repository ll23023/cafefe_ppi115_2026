package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import java.math.BigDecimal;
import java.util.UUID;

public class PagoDetalleRequest {

    public UUID idPago;
    public BigDecimal monto;
    public String tipoPago;
    public String observaciones;
    public String referenciaExterna;
}