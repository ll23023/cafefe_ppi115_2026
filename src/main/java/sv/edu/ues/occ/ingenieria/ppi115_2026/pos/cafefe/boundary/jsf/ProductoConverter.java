package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;

/**
 * @author mil
 */
@FacesConverter(forClass = Producto.class, managed = true)
public class ProductoConverter extends DefaultConverter<Producto, UUID> {

    @Inject
    private ProductoRepository productoRepository;

    @Override
    protected ProductoRepository getRepository() {
        return productoRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(Producto entidad) {
        return entidad.getIdProducto();
    }
}