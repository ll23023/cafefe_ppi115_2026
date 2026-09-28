package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

@FacesConverter(forClass = TipoCaracteristica.class, managed = true)
public class TipoCaracteristicaConverter extends DefaultConverter<TipoCaracteristica, UUID> {
    @Inject
    private TipoCaracteristicaRepository repository;

    @Override
    protected TipoCaracteristicaRepository getRepository() {
        return repository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(TipoCaracteristica entity) {
        return entity.getIdTipoCaracteristica();
    }
}
