package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoRolRepositoryTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private EmpleadoRolRepository repositorio;

    @Test
    void getEntityManager_devuelveElInyectado() {
        assertSame(em, repositorio.getEntityManager());
    }
}