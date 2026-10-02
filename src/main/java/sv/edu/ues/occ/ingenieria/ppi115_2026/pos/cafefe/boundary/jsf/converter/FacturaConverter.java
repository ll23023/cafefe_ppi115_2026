package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;

/**
 * @author mil
 */
@FacesConverter(forClass = Factura.class, managed = true)
public class FacturaConverter extends DefaultConverter<Factura, UUID> {

    @Inject
    private FacturaRepository facturaRepository;

    @Override
    protected FacturaRepository getRepository() {
        return facturaRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(Factura entidad) {
        return entidad.getIdFactura();
    }
}