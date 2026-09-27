package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

/**
 * Formatos de texto admitidos por {@link ColumnaTabla}.
 *
 * @author mikelb
 */
public enum FORMATO_COLUMNA {
    /** Muestra el valor mediante {@code toString()}. */
    TEXT,
    /** Muestra un {@link java.math.BigDecimal} como moneda local. */
    CURRENCY,
    /** Muestra valores temporales mediante su representación textual actual. */
    DATE,
    /** Convierte {@code true}/{@code false} en {@code ACTIVO}/{@code INACTIVO}. */
    BOOLEAN
}
