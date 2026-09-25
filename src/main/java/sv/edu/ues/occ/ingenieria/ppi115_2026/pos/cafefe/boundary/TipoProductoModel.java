package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

@Named
@ViewScoped
public class TipoProductoModel extends DefaultModel<TipoProducto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoProductoRepository tipoProductoRepository;

    @Override
    public TipoProductoRepository getRepository() {
        return tipoProductoRepository;
    }

    @Override
    public TipoProducto instanciarRegistro() {
        TipoProducto r = new TipoProducto(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        return r;
    }

    @Override
    public TipoProducto getRegistroById(Object id) {
        if (id != null && this.registros != null && !this.registros.isEmpty()) {
            UUID busca = (UUID) id;
            return this.registros.stream()
                    .filter(r -> r.getIdTipoProducto().equals(busca))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Object getIdByRegistro(TipoProducto dato) {
        if (dato != null) {
            return dato.getIdTipoProducto();
        }
        return null;
    }

    /**
     * Debes agregar estos dos métodos para que te autocomplete NetBeans a la hora de crear la tabla, si no, lo tendrás que hacer a mano y da espacio a errores.
     * @return 
     */
    @Override
    public TipoProducto getRegistro() {
        return super.getRegistro(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public List<TipoProducto> getRegistros() {
        return super.getRegistros(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    
}

