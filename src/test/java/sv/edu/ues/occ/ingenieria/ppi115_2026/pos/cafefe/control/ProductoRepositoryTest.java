package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class ProductoRepositoryTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Producto> query;

    @InjectMocks
    private ProductoRepository repositorio;

    @Test
    void getEntityManager_devuelveElInyectado() {
        assertSame(em, repositorio.getEntityManager());
    }

    @Test
    void findByNombre_usaLaNamedQueryYDevuelveLaLista() {
        Producto p = new Producto();
        when(em.createNamedQuery("Producto.findByNombre", Producto.class)).thenReturn(query);
        when(query.setParameter("nombre", "Café")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(p));

        List<Producto> resultado = repositorio.findByNombre("Café");

        assertEquals(1, resultado.size());
        assertSame(p, resultado.get(0));
        verify(query, times(1)).setParameter("nombre", "Café");
    }

    @Test
    void findByNombre_sinCoincidencias_devuelveListaVacia() {
        when(em.createNamedQuery("Producto.findByNombre", Producto.class)).thenReturn(query);
        when(query.setParameter("nombre", "Inexistente")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        assertTrue(repositorio.findByNombre("Inexistente").isEmpty());
    }
}