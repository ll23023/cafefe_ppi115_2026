package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CajaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caja;

/**
 * @author mil
 */
@FacesConverter(forClass = Caja.class, managed = true)
public class CajaConverter extends DefaultConverter<Caja, UUID> {

    @Inject
    private CajaRepository cajaRepository;

    @Override
    protected CajaRepository getRepository() {
        return cajaRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(Caja entidad) {
        return entidad.getIdCaja();
    }
}