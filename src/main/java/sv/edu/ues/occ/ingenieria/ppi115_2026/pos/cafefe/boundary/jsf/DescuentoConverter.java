package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Descuento;

@FacesConverter(forClass = Descuento.class, managed = true)
public class DescuentoConverter extends DefaultConverter<Descuento, UUID> {

    @Inject
    private DescuentoRepository descuentoRepository;

    @Override
    protected DescuentoRepository getRepository() {
        return descuentoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(Descuento entidad) {
        return entidad.getIdDescuento();
    }
}
