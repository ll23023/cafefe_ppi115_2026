package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import jakarta.faces.context.FacesContext;
/**
*@author mil
*/
@Named
@SessionScoped
public class SesionUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String IDIOMA_DEFECTO = "es";

    /** texto visible en el combo -> código de idioma. */
    private static final Map<String, String> IDIOMAS = new LinkedHashMap<>();
    static {
        IDIOMAS.put("Español", "es");
        IDIOMAS.put("English", "en");
        IDIOMAS.put("Português", "pt");
        IDIOMAS.put("Français", "fr");
    }

    private String localidad = IDIOMA_DEFECTO;

    
    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
    this.localidad = IDIOMAS.containsValue(localidad) ? localidad : IDIOMA_DEFECTO;

    FacesContext fc = FacesContext.getCurrentInstance();
    if (fc != null && fc.getViewRoot() != null) {
        fc.getViewRoot().setLocale(getLocale());
    }
}

    public Map<String, String> getListaIdiomas() {
        return IDIOMAS;
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(localidad);
    }
}