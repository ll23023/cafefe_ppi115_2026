package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 * @author mil
 */
@FacesConverter(forClass = Rol.class, managed = true)
public class RolConverter extends DefaultConverter<Rol, UUID> {

    @Inject
    private RolRepository rolRepository;

    @Override
    protected RolRepository getRepository() {
        return rolRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(Rol entidad) {
        return entidad.getIdRol();
    }
}