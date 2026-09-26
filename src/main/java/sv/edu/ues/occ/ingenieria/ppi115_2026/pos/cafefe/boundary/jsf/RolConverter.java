package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 * 
 * @author mil
 */
@FacesConverter(forClass = Rol.class, managed = true)
public class RolConverter implements Converter<Rol> {

    @Inject
    private RolRepository rolRepository;

    @Override
    public Rol getAsObject(FacesContext ctx, UIComponent comp, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return rolRepository.findById(UUID.fromString(value));
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Rol rol) {
        if (rol == null || rol.getIdRol() == null) {
            return "";
        }
        return rol.getIdRol().toString();
    }
}