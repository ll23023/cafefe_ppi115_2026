package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Descuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento;

@Named
@ViewScoped
public class DescuentoModel extends DefaultModel<Descuento, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private DescuentoRepository descuentoRepository;
    @Inject
    private TipoDescuentoRepository tipoDescuentoRepository;

    private List<TipoDescuento> tiposDescuento;
    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("idDescuento", "ID"),
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("idTipoDescuento.nombre", "Tipo"),
            new ColumnaTabla("fechaDesde", "Desde", FORMATO_COLUMNA.DATE),
            new ColumnaTabla("fechaHasta", "Hasta", FORMATO_COLUMNA.DATE),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    @Override
    public DescuentoRepository getRepository() {
        return descuentoRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        tiposDescuento = tipoDescuentoRepository.findAll();
    }

    @Override
    public Descuento instanciarRegistro() {
        return new Descuento(UUID.randomUUID());
    }

    @Override
    public Descuento getRegistroById(Object id) {
        return id == null ? null : descuentoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Descuento dato) {
        return dato == null ? null : dato.getIdDescuento();
    }

    public List<TipoDescuento> getTiposDescuento() {
        return tiposDescuento;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}
