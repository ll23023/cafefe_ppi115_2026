package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

/**
 * @author mil
 */
@FacesConverter(forClass = TipoCaracteristica.class, managed = true)
public class TipoCaracteristicaConverter extends DefaultConverter<TipoCaracteristica, UUID> {

    @Inject
    private TipoCaracteristicaRepository tipoCaracteristicaRepository;

    @Override
    protected TipoCaracteristicaRepository getRepository() {
        return tipoCaracteristicaRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(TipoCaracteristica entidad) {
        return entidad.getIdTipoCaracteristica();
    }
}