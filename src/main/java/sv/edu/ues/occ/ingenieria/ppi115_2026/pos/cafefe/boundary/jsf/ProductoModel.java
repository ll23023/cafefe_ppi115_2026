package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class ProductoModel extends DefaultModel<Producto, UUID> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProductoRepository productoRepository;

    private String filtroNombre;
    private BigDecimal filtroPrecioMin;
    private BigDecimal filtroPrecioMax;

     @Override
    public Producto instanciarRegistro() {
        Producto p = new Producto(UUID.randomUUID());
        p.setActivo(Boolean.TRUE);
        return p;
    }

    @Override
    public Producto getRegistroById(Object id) {
        return productoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Producto dato) {
        return dato != null ? dato.getIdProducto() : null;
    }

    @Override
    public ProductoRepository getRepository() {
        return productoRepository;
    }

    public void buscarPorNombre() {
        if (filtroNombre != null && !filtroNombre.isEmpty()) {
            this.registros = productoRepository.findByNombreLike(filtroNombre);
        } else {
            this.cargarRegistros();
        }
    }

    public void buscarActivos() {
        this.registros = productoRepository.findActivos();
    }

    public void buscarPorPrecio() {
        if (filtroPrecioMin != null && filtroPrecioMax != null) {
            this.registros = productoRepository.findByPrecioRange(filtroPrecioMin, filtroPrecioMax);
        } else {
            this.cargarRegistros();
        }
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public BigDecimal getFiltroPrecioMin() {
        return filtroPrecioMin;
    }

    public void setFiltroPrecioMin(BigDecimal filtroPrecioMin) {
        this.filtroPrecioMin = filtroPrecioMin;
    }

    public BigDecimal getFiltroPrecioMax() {
        return filtroPrecioMax;
    }

    public void setFiltroPrecioMax(BigDecimal filtroPrecioMax) {
        this.filtroPrecioMax = filtroPrecioMax;
    }
}
