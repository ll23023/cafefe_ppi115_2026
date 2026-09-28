package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoCaracteristica;

@FacesConverter(forClass = ProductoCaracteristica.class, managed = true)
public class ProductoCaracteristicaConverter extends DefaultConverter<ProductoCaracteristica, UUID> {

    @Inject
    private ProductoCaracteristicaRepository productoCaracteristicaRepository;

    @Override
    protected ProductoCaracteristicaRepository getRepository() {
        return productoCaracteristicaRepository;
    }

    @Override
    protected UUID parseId(String value) {
        return UUID.fromString(value);
    }

    @Override
    protected UUID getId(ProductoCaracteristica entidad) {
        return entidad.getIdProductoCaracteristica();
    }
}
