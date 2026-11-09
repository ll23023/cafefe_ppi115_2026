package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import java.math.BigDecimal;
import java.util.UUID;

public class FacturaOrdenProductoRequest {

    public UUID idFactura;
    public UUID idOrdenProducto;
    public BigDecimal precio;
    public String observaciones;
}