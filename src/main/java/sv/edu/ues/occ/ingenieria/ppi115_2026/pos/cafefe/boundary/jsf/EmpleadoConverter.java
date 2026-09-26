package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;

/**
 * 
 * @author mil
 */
@FacesConverter(forClass = Empleado.class, managed = true)
public class EmpleadoConverter implements Converter<Empleado> {

    @Inject
    private EmpleadoRepository empleadoRepository;

    @Override
    public Empleado getAsObject(FacesContext ctx, UIComponent comp, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return empleadoRepository.findById(UUID.fromString(value));
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Empleado emp) {
        if (emp == null || emp.getIdEmpleado() == null) {
            return "";
        }
        return emp.getIdEmpleado().toString();
    }
}