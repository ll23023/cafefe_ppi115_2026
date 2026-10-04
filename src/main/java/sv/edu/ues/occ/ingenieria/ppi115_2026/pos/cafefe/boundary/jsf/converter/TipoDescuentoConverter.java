package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento;

@FacesConverter(forClass = TipoDescuento.class, managed = true)
public class TipoDescuentoConverter extends DefaultConverter<TipoDescuento, UUID> {

    @Inject
    private TipoDescuentoRepository tipoDescuentoRepository;

    @Override
    protected TipoDescuentoRepository getRepository() {
        return tipoDescuentoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(TipoDescuento entidad) {
        return entidad.getIdTipoDescuento();
    }
}
