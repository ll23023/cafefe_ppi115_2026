package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;

/**
 * @author mil
 */
@FacesConverter(forClass = Empleado.class, managed = true)
public class EmpleadoConverter extends DefaultConverter<Empleado, UUID> {

    @Inject
    private EmpleadoRepository empleadoRepository;

    @Override
    protected EmpleadoRepository getRepository() {
        return empleadoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(Empleado entidad) {
        return entidad.getIdEmpleado();
    }
}