package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;

/**
 * 
 * @author mil
 */
@FacesConverter(forClass = EmpleadoRol.class, managed = true)
public class EmpleadoRolConverter implements Converter<EmpleadoRol> {

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    @Override
    public EmpleadoRol getAsObject(FacesContext ctx, UIComponent comp, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return empleadoRolRepository.findById(UUID.fromString(value));
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, EmpleadoRol er) {
        if (er == null || er.getIdEmpleadoRol() == null) {
            return "";
        }
        return er.getIdEmpleadoRol().toString();
    }
}