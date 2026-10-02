package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.DescuentoProducto;

@FacesConverter(forClass = DescuentoProducto.class, managed = true)
public class DescuentoProductoConverter extends DefaultConverter<DescuentoProducto, UUID> {

    @Inject
    private DescuentoProductoRepository descuentoProductoRepository;

    @Override
    protected DescuentoProductoRepository getRepository() {
        return descuentoProductoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(DescuentoProducto entidad) {
        return entidad.getIdDescuentoProducto();
    }
}
