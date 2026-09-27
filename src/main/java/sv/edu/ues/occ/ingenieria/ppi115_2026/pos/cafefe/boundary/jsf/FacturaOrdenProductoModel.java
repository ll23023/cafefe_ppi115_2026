package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaOrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.FacturaOrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.OrdenProducto;

/**
 * 
 * @author mil
 */
@Named
@ViewScoped
public class FacturaOrdenProductoModel extends DefaultModel<FacturaOrdenProducto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacturaOrdenProductoRepository repository;

    @Inject
    private FacturaRepository facturaRepository;

    @Inject
    private OrdenProductoRepository ordenProductoRepository;

    private List<Factura> facturas;
    private List<OrdenProducto> ordenProductos;
    private List<ColumnaTabla> columnas;

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    @Override
    public FacturaOrdenProducto instanciarRegistro() {
        FacturaOrdenProducto f = new FacturaOrdenProducto();
        f.setIdFacturaOrdenProducto(UUID.randomUUID());
        f.setObservaciones("");
        return f;
    }

    @Override
    public FacturaOrdenProducto getRegistroById(Object id) {
        return id == null ? null : repository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(FacturaOrdenProducto dato) {
        return dato != null ? dato.getIdFacturaOrdenProducto() : null;
    }

    @Override
    public DefaultRepository<FacturaOrdenProducto, UUID> getRepository() {
        return repository;
    }

    // Carga perezosa para el combo de Factura.
    public List<Factura> getFacturas() {
        if (this.facturas == null) {
            this.facturas = facturaRepository != null ? facturaRepository.findAll() : new ArrayList<>();
        }
        return facturas;
    }

    public void setFacturas(List<Factura> facturas) {
        this.facturas = facturas;
    }

    /**
     * Carga perezosa para el combo de OrdenProducto. El itemLabel usa
     * precio/observaciones porque todavía no existe Producto.java con un
     * campo de nombre; conviene cambiarlo por el nombre real del producto
     * en cuanto esa entidad esté disponible.
     */
    public List<OrdenProducto> getOrdenProductos() {
        if (this.ordenProductos == null) {
            this.ordenProductos = ordenProductoRepository != null
                    ? ordenProductoRepository.findAll()
                    : new ArrayList<>();
        }
        return ordenProductos;
    }

    public void setOrdenProductos(List<OrdenProducto> ordenProductos) {
        this.ordenProductos = ordenProductos;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("idFactura.cliente", "Factura de"),
                    new ColumnaTabla("idOrdenProducto", "Producto"),
                    new ColumnaTabla("precio", "Precio", FORMATO_COLUMNA.CURRENCY),
                    new ColumnaTabla("observaciones", "Observaciones")
            );
        }
        return columnas;
    }
}