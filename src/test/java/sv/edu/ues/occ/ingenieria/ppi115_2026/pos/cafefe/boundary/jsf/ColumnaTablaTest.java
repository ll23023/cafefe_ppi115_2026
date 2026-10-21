package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 * @author mil
 */
class ColumnaTablaTest {

    public static class Fila {

        private String texto;
        private Boolean activo;
        private BigDecimal precio;
        private LocalDate fecha;
        private Date fechaClasica;
        private Fila hijo;

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }

        public Boolean getActivo() {
            return activo;
        }

        public void setActivo(Boolean activo) {
            this.activo = activo;
        }

        public BigDecimal getPrecio() {
            return precio;
        }

        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public void setFecha(LocalDate fecha) {
            this.fecha = fecha;
        }

        public Date getFechaClasica() {
            return fechaClasica;
        }

        public void setFechaClasica(Date fechaClasica) {
            this.fechaClasica = fechaClasica;
        }

        public Fila getHijo() {
            return hijo;
        }

        public void setHijo(Fila hijo) {
            this.hijo = hijo;
        }
    }

    @Test
    void constructorDeDosParametros_usaFormatoTextPorDefecto() {
        ColumnaTabla c = new ColumnaTabla("texto", "Texto");

        assertEquals("texto", c.getProperty());
        assertEquals("Texto", c.getHeader());
        assertEquals(FORMATO_COLUMNA.TEXT, c.getFormat());
    }

    @Test
    void constructorConFormato_conservaElFormato() {
        ColumnaTabla c = new ColumnaTabla("precio", "Precio", FORMATO_COLUMNA.CURRENCY);

        assertEquals(FORMATO_COLUMNA.CURRENCY, c.getFormat());
    }

    @Test
    void constructorConFormatoNulo_usaText() {
        ColumnaTabla c = new ColumnaTabla("texto", "Texto", null);

        assertEquals(FORMATO_COLUMNA.TEXT, c.getFormat());
    }

    // getValue: un caso por formato

    @Test
    void getValue_text_devuelveElTexto() {
        Fila fila = new Fila();
        fila.setTexto("Café");

        assertEquals("Café", new ColumnaTabla("texto", "Texto").getValue(fila));
    }

    @Test
    void getValue_text_convierteNumerosATexto() {
        Fila fila = new Fila();
        fila.setPrecio(new BigDecimal("2.50"));

        assertEquals("2.50", new ColumnaTabla("precio", "Precio").getValue(fila));
    }

    @Test
    void getValue_boolean_verdaderoEsActivo() {
        Fila fila = new Fila();
        fila.setActivo(true);

        assertEquals("ACTIVO",
                new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN).getValue(fila));
    }

    @Test
    void getValue_boolean_falsoEsInactivo() {
        Fila fila = new Fila();
        fila.setActivo(false);

        assertEquals("INACTIVO",
                new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN).getValue(fila));
    }

    @Test
    void getValue_currency_formateaConDolar() {
        Fila fila = new Fila();
        fila.setPrecio(new BigDecimal("1234.5"));

        assertEquals("$1,234.50",
                new ColumnaTabla("precio", "Precio", FORMATO_COLUMNA.CURRENCY).getValue(fila));
    }

    @Test
    void getValue_date_conFechaModerna() {
        Fila fila = new Fila();
        fila.setFecha(LocalDate.of(2026, 10, 20));

        assertEquals("2026-10-20",
                new ColumnaTabla("fecha", "Fecha", FORMATO_COLUMNA.DATE).getValue(fila));
    }

    @Test
    void getValue_date_conFechaClasica() {
        Fila fila = new Fila();
        Date fecha = new Date();
        fila.setFechaClasica(fecha);

        assertEquals(fecha.toString(),
                new ColumnaTabla("fechaClasica", "Fecha", FORMATO_COLUMNA.DATE).getValue(fila));
    }

    // getValue: rutas anidadas, nulos y errores

    @Test
    void getValue_valorNulo_devuelveCadenaVacia() {
        assertEquals("", new ColumnaTabla("texto", "Texto").getValue(new Fila()));
    }

    @Test
    void getValue_filaNula_devuelveCadenaVacia() {
        assertEquals("", new ColumnaTabla("texto", "Texto").getValue(null));
    }

    @Test
    void getValue_rutaAnidada_leeLaPropiedadDelHijo() {
        Fila padre = new Fila();
        Fila hijo = new Fila();
        hijo.setTexto("Hijo");
        padre.setHijo(hijo);

        assertEquals("Hijo", new ColumnaTabla("hijo.texto", "Hijo").getValue(padre));
    }

    @Test
    void getValue_rutaAnidadaConSegmentoNulo_devuelveCadenaVacia() {
        Fila padre = new Fila();

        assertEquals("", new ColumnaTabla("hijo.texto", "Hijo").getValue(padre));
    }

    @Test
    void getValue_propiedadInexistente_lanzaExcepcionConElNombre() {
        ColumnaTabla c = new ColumnaTabla("noExiste", "X");
        Fila fila = new Fila();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> c.getValue(fila));

        assertTrue(ex.getMessage().contains("noExiste"));
        assertTrue(ex.getCause() instanceof ReflectiveOperationException);
    }
}