package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

/**
 * @author mil
 */
@FacesConverter(forClass = TipoProducto.class, managed = true)
public class TipoProductoConverter extends DefaultConverter<TipoProducto, UUID> {

    @Inject
    private TipoProductoRepository tipoProductoRepository;

    @Override
    protected TipoProductoRepository getRepository() {
        return tipoProductoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(TipoProducto entidad) {
        return entidad.getIdTipoProducto();
    }
}