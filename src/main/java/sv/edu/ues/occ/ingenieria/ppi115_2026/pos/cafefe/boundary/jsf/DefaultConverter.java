package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;

/**
 * Converter genérico reutilizable por cualquier entidad administrada por un
 * {@link DefaultRepository}. Centraliza la lógica repetida en los converters
 * concretos: "si el String viene vacío devolver null", "buscar por id" y
 * "convertir la entidad de vuelta a String usando su id".
 *
 * <p>JSF exige que cada {@code @FacesConverter(forClass = ...)} se declare en
 * una clase distinta (no es posible registrar un único converter para varias
 * entidades), por lo que no se puede eliminar del todo la clase por entidad.
 * Lo que sí se elimina es la duplicación de lógica: cada subclase concreta
 * solo indica su repositorio, cómo parsear el id recibido y cómo obtener el
 * id desde la entidad. Sigue el mismo espíritu que {@code DefaultModel} y
 * {@code DefaultRepository}.</p>
 *
 * @param <T>  tipo de entidad administrada
 * @param <ID> tipo del identificador de la entidad
 */
public abstract class DefaultConverter<T, ID> implements Converter<T> {

    /**
     * @return repositorio usado para buscar la entidad por su id
     */
    protected abstract DefaultRepository<T, ID> getRepository();

    /**
     * Convierte el texto recibido del componente JSF al tipo del id.
     *
     * @param value texto recibido; nunca es {@code null} ni está vacío
     * @return id ya convertido al tipo correcto
     */
    protected abstract ID parseId(String value);

    /**
     * @param entidad entidad de la que se extraerá el id
     * @return id de la entidad
     */
    protected abstract ID getId(T entidad);

    @Override
    public T getAsObject(FacesContext ctx, UIComponent comp, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return getRepository().findById(parseId(value));
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, T entidad) {
        if (entidad == null) {
            return "";
        }
        ID id = getId(entidad);
        return id != null ? id.toString() : "";
    }
}