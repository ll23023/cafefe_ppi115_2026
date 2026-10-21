package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class RolRepositoryTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Rol> query;

    @InjectMocks
    private RolRepository repositorio;

    @Test
    void getEntityManager_devuelveElInyectado() {
        assertSame(em, repositorio.getEntityManager());
    }

    @Test
    void findByNombre_usaLaNamedQueryYDevuelveLaLista() {
        Rol r = new Rol();
        when(em.createNamedQuery("Rol.findByNombre", Rol.class)).thenReturn(query);
        when(query.setParameter("nombre", "Cajero")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(r));

        List<Rol> resultado = repositorio.findByNombre("Cajero");

        assertEquals(1, resultado.size());
        assertSame(r, resultado.get(0));
        verify(query, times(1)).setParameter("nombre", "Cajero");
    }

    @Test
    void findByNombre_sinCoincidencias_devuelveListaVacia() {
        when(em.createNamedQuery("Rol.findByNombre", Rol.class)).thenReturn(query);
        when(query.setParameter("nombre", "Inexistente")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        assertTrue(repositorio.findByNombre("Inexistente").isEmpty());
    }
}