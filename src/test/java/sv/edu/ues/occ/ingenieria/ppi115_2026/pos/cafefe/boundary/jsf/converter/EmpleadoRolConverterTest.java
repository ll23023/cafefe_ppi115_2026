package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.converter;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoRolConverterTest {

    @Mock
    private EmpleadoRolRepository repositorio;

    @InjectMocks
    private EmpleadoRolConverter convertidor;

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
        assertEquals(id, convertidor.getId(new EmpleadoRol(id)));
    }
}