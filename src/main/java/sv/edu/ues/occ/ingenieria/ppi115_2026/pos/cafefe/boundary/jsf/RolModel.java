package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class RolModel extends DefaultModel<Rol, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private RolRepository rolRepository;

    /** Definición de columnas para {@code tablaDinamica}; se arma una sola vez. */
    private List<ColumnaTabla> columnas;

    @Override
    public RolRepository getRepository() {
        return rolRepository;
    }

    @Override
    public Rol instanciarRegistro() {
        Rol r = new Rol(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        return r;
    }

    @Override
    public Rol getRegistroById(Object id) {
        return id == null ? null : rolRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Rol dato) {
        return dato != null ? dato.getIdRol() : null;
    }

    /**
     * Columnas que se muestran en la tabla de roles. Se usa desde el xhtml
     * como {@code columns="#{rolModel.columnas}"}.
     *
     * @return columnas configuradas para {@code Rol}
     */
    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}