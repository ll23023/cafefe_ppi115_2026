package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class RolConverterTest {

    @Mock
    private RolRepository repositorio;

    @InjectMocks
    private RolConverter convertidor;

    @Test
    void getRepository_devuelveElInyectado() {
        assertSame(repositorio, convertidor.getRepository());
    }

    @Test
    void parseId_convierteTextoAUUID() {
        UUID id = UUID.randomUUID();
        assertEquals(id, convertidor.parseId(id.toString()));
    }

    @Test
    void parseId_conTextoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> convertidor.parseId("no-es-uuid"));
    }

    @Test
    void getId_devuelveElIdDeLaEntidad() {
        UUID id = UUID.randomUUID();
        assertEquals(id, convertidor.getId(new Rol(id)));
    }
}