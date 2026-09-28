package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

@Named
@ViewScoped
public class TipoCaracteristicaModel extends DefaultModel<TipoCaracteristica, UUID> {
    private static final long serialVersionUID = 1L;
    @Inject
    private TipoCaracteristicaRepository tipoCaracteristicaRepository;
    private List<ColumnaTabla> columnas;

    @Override
    public TipoCaracteristicaRepository getRepository() {
        return tipoCaracteristicaRepository;
    }

    @Override
    public TipoCaracteristica instanciarRegistro() {
        TipoCaracteristica tipo = new TipoCaracteristica(UUID.randomUUID());
        tipo.setActivo(Boolean.TRUE);
        tipo.setObservaciones("");
        return tipo;
    }

    @Override
    public TipoCaracteristica getRegistroById(Object id) {
        return id == null ? null : tipoCaracteristicaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(TipoCaracteristica dato) {
        return dato == null ? null : dato.getIdTipoCaracteristica();
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null)
            columnas = List.of(new ColumnaTabla("idTipoCaracteristica", "ID"), new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("expresionRegular", "Expresión regular"),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("observaciones", "Observaciones"));
        return columnas;
    }
}
