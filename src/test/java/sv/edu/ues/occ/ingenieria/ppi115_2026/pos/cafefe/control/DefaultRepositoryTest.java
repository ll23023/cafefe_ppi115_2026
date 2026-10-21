package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class DefaultRepositoryTest {

    @Mock
    private EntityManager em;

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private PersistenceUnitUtil util;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Producto> cq;

    @Mock
    private CriteriaQuery<Long> cqLong;

    @Mock
    private Root<Producto> root;

    @Mock
    private Expression<Long> expresionCount;

    @Mock
    private TypedQuery<Producto> query;

    @Mock
    private TypedQuery<Long> queryLong;

    private DefaultRepository<Producto, UUID> repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new DefaultRepository<Producto, UUID>(Producto.class) {
            @Override
            protected EntityManager getEntityManager() {
                return em;
            }
        };
    }

    // crear y modificar

    @Test
    void crear_llamaPersist() {
        Producto p = new Producto(UUID.randomUUID());

        repositorio.crear(p);

        verify(em, times(1)).persist(p);
    }

    @Test
    void modificar_devuelveLoQueRegresaMerge() {
        Producto original = new Producto(UUID.randomUUID());
        Producto mezclado = new Producto(UUID.randomUUID());
        when(em.merge(original)).thenReturn(mezclado);

        Producto resultado = repositorio.modificar(original);

        assertSame(mezclado, resultado);
    }

    // eliminar: todas las ramas

    @Test
    void eliminar_conEntidadNula_noHaceNada() {
        repositorio.eliminar(null);

        verifyNoInteractions(em);
    }

    @Test
    void eliminar_entidadAdministrada_laRemueveDirecto() {
        Producto p = new Producto(UUID.randomUUID());
        when(em.contains(p)).thenReturn(true);

        repositorio.eliminar(p);

        verify(em, times(1)).remove(p);
        verify(em, never()).find(any(), any());
    }

    @Test
    void eliminar_entidadDesconectadaSinId_noRemueve() {
        Producto p = new Producto();
        when(em.contains(p)).thenReturn(false);
        when(em.getEntityManagerFactory()).thenReturn(emf);
        when(emf.getPersistenceUnitUtil()).thenReturn(util);
        when(util.getIdentifier(p)).thenReturn(null);

        repositorio.eliminar(p);

        verify(em, never()).remove(any());
        verify(em, never()).find(any(), any());
    }

    @Test
    void eliminar_entidadDesconectada_buscaLaAdministradaYLaRemueve() {
        UUID id = UUID.randomUUID();
        Producto desconectado = new Producto(id);
        Producto administrado = new Producto(id);
        when(em.contains(desconectado)).thenReturn(false);
        when(em.getEntityManagerFactory()).thenReturn(emf);
        when(emf.getPersistenceUnitUtil()).thenReturn(util);
        when(util.getIdentifier(desconectado)).thenReturn(id);
        when(em.find(Producto.class, id)).thenReturn(administrado);

        repositorio.eliminar(desconectado);

        verify(em, times(1)).remove(administrado);
    }

    @Test
    void eliminar_entidadDesconectadaQueYaNoExiste_noRemueve() {
        UUID id = UUID.randomUUID();
        Producto desconectado = new Producto(id);
        when(em.contains(desconectado)).thenReturn(false);
        when(em.getEntityManagerFactory()).thenReturn(emf);
        when(emf.getPersistenceUnitUtil()).thenReturn(util);
        when(util.getIdentifier(desconectado)).thenReturn(id);
        when(em.find(Producto.class, id)).thenReturn(null);

        repositorio.eliminar(desconectado);

        verify(em, never()).remove(any());
    }

    // búsquedas

    @Test
    void findById_buscaPorLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        Producto p = new Producto(id);
        when(em.find(Producto.class, id)).thenReturn(p);

        assertSame(p, repositorio.findById(id));
    }

    @Test
    void findById_noEncontrado_devuelveNull() {
        UUID id = UUID.randomUUID();
        when(em.find(Producto.class, id)).thenReturn(null);

        assertNull(repositorio.findById(id));
    }

    @Test
    void findAll_devuelveLaListaDelQuery() {
        Producto p = new Producto(UUID.randomUUID());
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Producto.class)).thenReturn(cq);
        when(cq.from(Producto.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(p));

        List<Producto> resultado = repositorio.findAll();

        assertEquals(1, resultado.size());
        assertSame(p, resultado.get(0));
        verify(cq, times(1)).select(root);
    }

    @Test
    void findRange_aplicaInicioYMaximo() {
        Producto p = new Producto(UUID.randomUUID());
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Producto.class)).thenReturn(cq);
        when(cq.from(Producto.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(5)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(p));

        List<Producto> resultado = repositorio.findRange(5, 10);

        assertEquals(1, resultado.size());
        verify(query, times(1)).setFirstResult(5);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void count_devuelveElTotalDelQuery() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cqLong);
        when(cqLong.from(Producto.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(expresionCount);
        when(em.createQuery(cqLong)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(7L);

        assertEquals(7L, repositorio.count());
        verify(cqLong, times(1)).select(expresionCount);
    }
}