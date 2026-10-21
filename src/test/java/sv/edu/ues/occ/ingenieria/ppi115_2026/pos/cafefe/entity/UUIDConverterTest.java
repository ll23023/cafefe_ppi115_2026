package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 * @author mil
 */
class UUIDConverterTest {

    private final UUIDConverter convertidor = new UUIDConverter();

    @Test
    void convertToDatabaseColumn_devuelveElMismoUUID() {
        UUID id = UUID.randomUUID();
        assertSame(id, convertidor.convertToDatabaseColumn(id));
    }

    @Test
    void convertToDatabaseColumn_conNulo_devuelveNulo() {
        assertNull(convertidor.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_devuelveElMismoUUID() {
        UUID id = UUID.randomUUID();
        assertSame(id, convertidor.convertToEntityAttribute(id));
    }

    @Test
    void convertToEntityAttribute_conNulo_devuelveNulo() {
        assertNull(convertidor.convertToEntityAttribute(null));
    }

    @Test
    void idaYVuelta_conservaElValor() {
        UUID id = UUID.randomUUID();
        UUID resultado = convertidor.convertToEntityAttribute(
                convertidor.convertToDatabaseColumn(id));
        assertEquals(id, resultado);
    }
}