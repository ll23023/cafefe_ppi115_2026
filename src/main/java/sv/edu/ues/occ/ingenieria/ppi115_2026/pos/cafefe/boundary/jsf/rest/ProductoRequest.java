package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductoRequest {

    public String nombre;
    public Boolean activo;
    public String comentarios;
    public BigDecimal precioSugerido;

    public List<CaracteristicaDto> caracteristicas = new ArrayList<>();
    public List<String> tiposProducto = new ArrayList<>();
    public List<DescuentoDto> descuentos = new ArrayList<>();

    public static class CaracteristicaDto {

        public String tipo;
        public String nombre;
        public String valor;
    }

    public static class DescuentoDto {

        public String tipoDescuento;
        public String nombre;
        public Integer valor;
        public Date fechaDesde;
        public Date fechaHasta;
    }
}
