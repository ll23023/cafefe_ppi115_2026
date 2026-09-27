package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Define una columna que puede ser renderizada por{@code tablaDinamica}.
 * 
 * @author mikelb
 */
public class ColumnaTabla implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String property;
    private final String header;
    private final FORMATO_COLUMNA format;

    /**
     * Crea una columna de texto.
     *
     * @param property propiedad o ruta de propiedades que se leerá en cada fila
     * @param header texto visible en el encabezado
     */
    public ColumnaTabla(String property, String header) {
        this(property, header, FORMATO_COLUMNA.TEXT);
    }

    /**
     * Crea una columna con formato explícito.
     *
     * @param property propiedad o ruta anidada, separada por puntos
     * @param header texto visible en el encabezado de la columna.
     * @param format formato del texto de la columna; si es {@code null}, se usa {@code TEXT}
     */
    public ColumnaTabla(String property, String header, FORMATO_COLUMNA format) {
        this.property = property;
        this.header = header;
        this.format = format == null ? FORMATO_COLUMNA.TEXT : format;
    }

    public String getProperty() {
        return property;
    }

    public String getHeader() {
        return header;
    }

    public FORMATO_COLUMNA getFormat() {
        return format;
    }

    /**
     * Obtiene y formatea el valor de esta columna para una fila.
     *
     * @param row entidad que representa la fila actual
     * @return texto formateado; devuelve una cadena vacía si algún segmento
     * de la ruta es {@code null}
     * @throws IllegalArgumentException si la ruta no tiene un getter válido
     */
    public String getValue(Object row) {
        Object value = resolveProperty(row);
        if (value == null) {
            return "";
        }

        return switch (format) {
            case BOOLEAN -> Boolean.TRUE.equals(value) ? "ACTIVO" : "INACTIVO";
            case CURRENCY -> NumberFormat.getCurrencyInstance(Locale.getDefault())
                    .format((BigDecimal) value);
            case DATE -> value instanceof TemporalAccessor
                    ? value.toString()
                    : value.toString();
            case TEXT -> value.toString();
        };
    }

    /**
     * Resuelve la ruta de propiedades anidadas y obtiene el valor de la propiedad
     * final.
     *
     * @param row entidad que representa la fila actual
     * @return valor de la propiedad final, o {@code null} si algún segmento es
     * {@code null}
     */
    private Object resolveProperty(Object row) {
        Object value = row;
        for (String part : property.split("\\.")) {
            if (value == null) {
                return null;
            }
            value = invokeGetter(value, part);
        }
        return value;
    }

    /**
     * Invoca el getter de una propiedad en un objeto.
     *
     * @param target objeto que contiene la propiedad
     * @param propertyName nombre de la propiedad
     * @return valor de la propiedad
     * @throws IllegalArgumentException si no se puede invocar el getter
     */
    private Object invokeGetter(Object target, String propertyName) {
        String suffix = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        try {
            Method getter = target.getClass().getMethod("get" + suffix);
            return getter.invoke(target);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("No se pudo leer la propiedad: " + property, exception);
        }
    }
}
