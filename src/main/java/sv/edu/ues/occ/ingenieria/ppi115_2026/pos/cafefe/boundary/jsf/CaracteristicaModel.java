package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

@Named
@ViewScoped
public class CaracteristicaModel extends DefaultModel<Caracteristica, UUID> {
    private static final long serialVersionUID = 1L;
    @Inject
    private CaracteristicaRepository caracteristicaRepository;
    @Inject
    private TipoCaracteristicaRepository tipoCaracteristicaRepository;
    private List<TipoCaracteristica> tiposCaracteristica;
    private List<ColumnaTabla> columnas;

    @Override
    public CaracteristicaRepository getRepository() {
        return caracteristicaRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        tiposCaracteristica = tipoCaracteristicaRepository.findAll();
    }

    @Override
    public Caracteristica instanciarRegistro() {
        Caracteristica caracteristica = new Caracteristica(UUID.randomUUID());
        caracteristica.setActivo(Boolean.TRUE);
        caracteristica.setObservaciones("");
        return caracteristica;
    }

    @Override
    public Caracteristica getRegistroById(Object id) {
        return id == null ? null : caracteristicaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Caracteristica dato) {
        return dato == null ? null : dato.getIdCaracteristica();
    }

    public List<TipoCaracteristica> getTiposCaracteristica() {
        return tiposCaracteristica;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null)
            columnas = List.of(new ColumnaTabla("idCaracteristica", "ID"), new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("idTipoCaracteristica.nombre", "Tipo"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("observaciones", "Observaciones"));
        return columnas;
    }
}
