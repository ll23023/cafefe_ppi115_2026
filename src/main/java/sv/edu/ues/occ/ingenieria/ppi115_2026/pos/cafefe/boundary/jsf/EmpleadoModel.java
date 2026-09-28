package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class EmpleadoModel extends DefaultModel<Empleado, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmpleadoRepository empleadoRepository;

    private List<ColumnaTabla> columnas;

    @Override
    public EmpleadoRepository getRepository() {
        return empleadoRepository;
    }

    @Override
    public Empleado instanciarRegistro() {
        Empleado r = new Empleado(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setComentarios("");
        return r;
    }

    @Override
    public Empleado getRegistroById(Object id) {
        return id == null ? null : empleadoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Empleado dato) {
        return dato != null ? dato.getIdEmpleado() : null;
    }

    /**
     * Columnas que se muestran en la tabla de empleados. Se usa desde el
     * xhtml como {@code columns="#{empleadoModel.columnas}"}.
     *
     * @return columnas configuradas para {@code Empleado}
     */
    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("apellido", "Apellido"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("comentarios", "Comentarios"));
        }
        return columnas;
    }
}