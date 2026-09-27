package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;

/**
 * @author mil
 */
@FacesConverter(forClass = EmpleadoRol.class, managed = true)
public class EmpleadoRolConverter extends DefaultConverter<EmpleadoRol, UUID> {

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    @Override
    protected EmpleadoRolRepository getRepository() {
        return empleadoRolRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(EmpleadoRol entidad) {
        return entidad.getIdEmpleadoRol();
    }
}