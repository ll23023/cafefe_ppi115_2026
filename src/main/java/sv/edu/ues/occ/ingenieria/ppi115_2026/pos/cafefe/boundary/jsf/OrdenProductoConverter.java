package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.OrdenProducto;

/**
 * @author mil
 */
@FacesConverter(forClass = OrdenProducto.class, managed = true)
public class OrdenProductoConverter extends DefaultConverter<OrdenProducto, UUID> {

    @Inject
    private OrdenProductoRepository ordenProductoRepository;

    @Override
    protected OrdenProductoRepository getRepository() {
        return ordenProductoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(OrdenProducto entidad) {
        return entidad.getIdOrdenProducto();
    }
}