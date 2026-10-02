package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter.DefaultConverter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Pago;

/**
 * @author mil
 */
@FacesConverter(forClass = Pago.class, managed = true)
public class PagoConverter extends DefaultConverter<Pago, UUID> {

    @Inject
    private PagoRepository pagoRepository;

    @Override
    protected PagoRepository getRepository() {
        return pagoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(Pago entidad) {
        return entidad.getIdPago();
    }
}