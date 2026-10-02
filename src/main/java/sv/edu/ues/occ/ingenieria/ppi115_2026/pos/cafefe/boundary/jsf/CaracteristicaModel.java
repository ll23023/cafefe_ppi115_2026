package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class CaracteristicaModel extends DefaultModel<Caracteristica, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private CaracteristicaRepository caracteristicaRepository;

    @Inject
    private TipoCaracteristicaRepository tipoCaracteristicaRepository;

    private List<TipoCaracteristica> tipos;
    private List<ColumnaTabla> columnas;

    @Override
    public CaracteristicaRepository getRepository() {
        return caracteristicaRepository;
    }

    @Override
    public Caracteristica instanciarRegistro() {
        Caracteristica c = new Caracteristica(UUID.randomUUID());
        c.setActivo(Boolean.TRUE);
        c.setObservaciones("");
        return c;
    }

    @Override
    public Caracteristica getRegistroById(Object id) {
        return id == null ? null : caracteristicaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Caracteristica dato) {
        return dato != null ? dato.getIdCaracteristica() : null;
    }

    public List<TipoCaracteristica> getTipos() {
        if (this.tipos == null) {
            this.tipos = tipoCaracteristicaRepository != null
                    ? tipoCaracteristicaRepository.findAll().stream()
                            .filter(t -> Boolean.TRUE.equals(t.getActivo()))
                            .toList()
                    : new ArrayList<>();
        }
        return tipos;
    }

    public void setTipos(List<TipoCaracteristica> tipos) {
        this.tipos = tipos;
    }

    @Override
    public void btnGuardarHandler() {
        if (this.registro != null
                && this.registro.getIdTipoCaracteristica() != null
                && !Boolean.TRUE.equals(this.registro.getIdTipoCaracteristica().getActivo())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se puede asignar un tipo de característica inactivo.", null));
            return;
        }
        super.btnGuardarHandler();
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("idTipoCaracteristica.nombre", "Tipo"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}