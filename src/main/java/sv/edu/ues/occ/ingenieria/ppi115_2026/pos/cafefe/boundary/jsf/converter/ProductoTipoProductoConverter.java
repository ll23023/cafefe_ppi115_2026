package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoTipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoTipoProducto;

@FacesConverter(forClass = ProductoTipoProducto.class, managed = true)
public class ProductoTipoProductoConverter extends DefaultConverter<ProductoTipoProducto, UUID> {
    @Inject
    private ProductoTipoProductoRepository repository;

    @Override
    protected ProductoTipoProductoRepository getRepository() {
        return repository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(ProductoTipoProducto entity) {
        return entity.getIdProductoTipoProducto();
    }
}
