package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DefaultRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class DefaultModelTest {

    /**
     * Implementación mínima de la clase abstracta para poder probarla.
     */
    static class ModeloPrueba extends DefaultModel<Producto, UUID> {

        private static final long serialVersionUID = 1L;

        transient DefaultRepository<Producto, UUID> repo;

        @Override
        public Producto instanciarRegistro() {
            return new Producto(UUID.randomUUID());
        }

        @Override
        public Producto getRegistroById(Object id) {
            return null;
        }

        @Override
        public Object getIdByRegistro(Producto dato) {
            return dato == null ? null : dato.getIdProducto();
        }

        @Override
        public DefaultRepository<Producto, UUID> getRepository() {
            return repo;
        }
    }

    @Mock
    private DefaultRepository<Producto, UUID> repositorio;

    private ModeloPrueba modelo;

    @BeforeEach
    void setUp() {
        modelo = new ModeloPrueba();
        modelo.repo = repositorio;
    }

    @Test
    void init_cargaRegistrosYPreparaElFormularioEnCrear() {
        List<Producto> lista = new ArrayList<>();
        when(repositorio.findAll()).thenReturn(lista);

        modelo.init();

        assertSame(lista, modelo.getRegistros());
        assertNotNull(modelo.getRegistro());
        assertNull(modelo.getSeleccion());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void cargarRegistros_conRepositorio_usaFindAll() {
        List<Producto> lista = new ArrayList<>();
        when(repositorio.findAll()).thenReturn(lista);

        modelo.cargarRegistros();

        assertSame(lista, modelo.getRegistros());
    }

    @Test
    void cargarRegistros_sinRepositorio_dejaListaVacia() {
        modelo.repo = null;

        modelo.cargarRegistros();

        assertNotNull(modelo.getRegistros());
        assertTrue(modelo.getRegistros().isEmpty());
    }

    @Test
    void limpiar_reiniciaRegistroSeleccionYEstado() {
        modelo.setSeleccion(new Producto(UUID.randomUUID()));
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.limpiar();

        assertNotNull(modelo.getRegistro());
        assertNull(modelo.getSeleccion());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void btnNuevoHandler_dejaElFormularioEnCrear() {
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.btnNuevoHandler();

        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
        assertNotNull(modelo.getRegistro());
    }

    @Test
    void btnGuardarHandler_enCrear_llamaCrearYLimpia() {
        modelo.limpiar();
        Producto p = modelo.getRegistro();

        modelo.btnGuardarHandler();

        verify(repositorio, times(1)).crear(p);
        verify(repositorio, never()).modificar(any());
        verify(repositorio, times(1)).findAll();
        assertNotSame(p, modelo.getRegistro());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void btnGuardarHandler_enModificar_llamaModificar() {
        Producto p = new Producto(UUID.randomUUID());
        modelo.setRegistro(p);
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.btnGuardarHandler();

        verify(repositorio, times(1)).modificar(p);
        verify(repositorio, never()).crear(any());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void btnGuardarHandler_sinRegistro_noHaceNada() {
        modelo.setRegistro(null);

        modelo.btnGuardarHandler();

        verifyNoInteractions(repositorio);
    }

    @Test
    void btnGuardarHandler_sinRepositorio_noHaceNada() {
        Producto p = new Producto(UUID.randomUUID());
        modelo.setRegistro(p);
        modelo.repo = null;

        modelo.btnGuardarHandler();

        assertSame(p, modelo.getRegistro());
    }

    @Test
    void btnModificarHandler_modificaYLimpia() {
        Producto p = new Producto(UUID.randomUUID());
        modelo.setRegistro(p);

        modelo.btnModificarHandler();

        verify(repositorio, times(1)).modificar(p);
        verify(repositorio, times(1)).findAll();
        assertNotSame(p, modelo.getRegistro());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void btnModificarHandler_sinRegistro_noHaceNada() {
        modelo.setRegistro(null);

        modelo.btnModificarHandler();

        verifyNoInteractions(repositorio);
    }

    @Test
    void btnModificarHandler_sinRepositorio_noHaceNada() {
        Producto p = new Producto(UUID.randomUUID());
        modelo.setRegistro(p);
        modelo.repo = null;

        modelo.btnModificarHandler();

        assertSame(p, modelo.getRegistro());
    }

    @Test
    void btnEliminarHandler_eliminaYLimpia() {
        Producto p = new Producto(UUID.randomUUID());
        modelo.setRegistro(p);

        modelo.btnEliminarHandler();

        verify(repositorio, times(1)).eliminar(p);
        verify(repositorio, times(1)).findAll();
        assertNotSame(p, modelo.getRegistro());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void btnEliminarHandler_sinRegistro_noHaceNada() {
        modelo.setRegistro(null);

        modelo.btnEliminarHandler();

        verifyNoInteractions(repositorio);
    }

    @Test
    void btnEliminarHandler_sinRepositorio_noHaceNada() {
        Producto p = new Producto(UUID.randomUUID());
        modelo.setRegistro(p);
        modelo.repo = null;

        modelo.btnEliminarHandler();

        assertSame(p, modelo.getRegistro());
    }

    @Test
    void seleccionarRegistro_cargaElRegistroYPasaAModificar() {
        Producto p = new Producto(UUID.randomUUID());

        modelo.seleccionarRegistro(p);

        assertSame(p, modelo.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, modelo.getEstado());
    }

    @Test
    void seleccionarRegistro_conNulo_noCambiaNada() {
        modelo.seleccionarRegistro(null);

        assertNull(modelo.getRegistro());
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
    }

    @Test
    void btnEditarHandler_conSeleccion_cargaElFormulario() {
        Producto p = new Producto(UUID.randomUUID());
        modelo.setSeleccion(p);

        modelo.btnEditarHandler();

        assertSame(p, modelo.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, modelo.getEstado());
    }

    @Test
    void btnEditarHandler_sinSeleccion_noCambiaNada() {
        modelo.btnEditarHandler();

        assertNull(modelo.getRegistro());
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
    }

    // Estados y accesores

    @Test
    void isEditando_soloEsVerdaderoEnModificar() {
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);
        assertTrue(modelo.isEditando());

        modelo.setEstado(ESTADO_CRUD.CREAR);
        assertFalse(modelo.isEditando());
    }

    @Test
    void isCreando_soloEsVerdaderoEnCrear() {
        modelo.setEstado(ESTADO_CRUD.CREAR);
        assertTrue(modelo.isCreando());

        modelo.setEstado(ESTADO_CRUD.MODIFICAR);
        assertFalse(modelo.isCreando());
    }

    @Test
    void gettersYSetters() {
        List<Producto> lista = new ArrayList<>();
        Producto registro = new Producto(UUID.randomUUID());
        Producto seleccion = new Producto(UUID.randomUUID());

        modelo.setRegistros(lista);
        modelo.setRegistro(registro);
        modelo.setSeleccion(seleccion);
        modelo.setEstado(ESTADO_CRUD.NINGUNO);

        assertSame(lista, modelo.getRegistros());
        assertSame(registro, modelo.getRegistro());
        assertSame(seleccion, modelo.getSeleccion());
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
    }
}