package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaOrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.FacturaOrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.OrdenProducto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class FacturaOrdenProductoModelTest {

    @Mock
    private FacturaOrdenProductoRepository repository;

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private OrdenProductoRepository ordenProductoRepository;

    @InjectMocks
    private FacturaOrdenProductoModel modelo;

    @Test
    void getRepository_devuelveElInyectado() {
        assertSame(repository, modelo.getRepository());
    }

    @Test
    void init_cargaRegistrosYPreparaElFormulario() {
        List<FacturaOrdenProducto> lista = new ArrayList<>();
        when(repository.findAll()).thenReturn(lista);

        modelo.init();

        assertSame(lista, modelo.getRegistros());
        assertNotNull(modelo.getRegistro());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void instanciarRegistro_creaUnRegistroConId() {
        FacturaOrdenProducto f = modelo.instanciarRegistro();

        assertNotNull(f.getIdFacturaOrdenProducto());
        assertEquals("", f.getObservaciones());
    }

    @Test
    void getRegistroById_conNulo_devuelveNulo() {
        assertNull(modelo.getRegistroById(null));
        verifyNoInteractions(repository);
    }

    @Test
    void getRegistroById_conId_buscaEnElRepositorio() {
        UUID id = UUID.randomUUID();
        FacturaOrdenProducto f = new FacturaOrdenProducto(id);
        when(repository.findById(id)).thenReturn(f);

        assertSame(f, modelo.getRegistroById(id));
    }

    @Test
    void getIdByRegistro_devuelveElIdOnuloSiNoHayRegistro() {
        UUID id = UUID.randomUUID();

        assertEquals(id, modelo.getIdByRegistro(new FacturaOrdenProducto(id)));
        assertNull(modelo.getIdByRegistro(null));
    }

    @Test
    void getFacturas_cargaPerezosaUnaSolaVez() {
        List<Factura> facturas = new ArrayList<>();
        when(facturaRepository.findAll()).thenReturn(facturas);

        List<Factura> primera = modelo.getFacturas();
        List<Factura> segunda = modelo.getFacturas();

        assertSame(facturas, primera);
        assertSame(primera, segunda);
        verify(facturaRepository, times(1)).findAll();
    }

    @Test
    void getFacturas_sinRepositorio_devuelveListaVacia() {
        assertTrue(new FacturaOrdenProductoModel().getFacturas().isEmpty());
    }

    @Test
    void setFacturas_reemplazaLaLista() {
        List<Factura> facturas = new ArrayList<>();

        modelo.setFacturas(facturas);

        assertSame(facturas, modelo.getFacturas());
        verifyNoInteractions(facturaRepository);
    }

    @Test
    void getOrdenProductos_cargaPerezosaUnaSolaVez() {
        List<OrdenProducto> ordenProductos = new ArrayList<>();
        when(ordenProductoRepository.findAll()).thenReturn(ordenProductos);

        List<OrdenProducto> primera = modelo.getOrdenProductos();
        List<OrdenProducto> segunda = modelo.getOrdenProductos();

        assertSame(ordenProductos, primera);
        assertSame(primera, segunda);
        verify(ordenProductoRepository, times(1)).findAll();
    }

    @Test
    void getOrdenProductos_sinRepositorio_devuelveListaVacia() {
        assertTrue(new FacturaOrdenProductoModel().getOrdenProductos().isEmpty());
    }

    @Test
    void setOrdenProductos_reemplazaLaLista() {
        List<OrdenProducto> ordenProductos = new ArrayList<>();

        modelo.setOrdenProductos(ordenProductos);

        assertSame(ordenProductos, modelo.getOrdenProductos());
        verifyNoInteractions(ordenProductoRepository);
    }

    @Test
    void getColumnas_creaLaListaUnaSolaVez() {
        List<ColumnaTabla> primera = modelo.getColumnas();
        List<ColumnaTabla> segunda = modelo.getColumnas();

        assertEquals(4, primera.size());
        assertSame(primera, segunda);
        assertEquals("precio", primera.get(2).getProperty());
        assertEquals(FORMATO_COLUMNA.CURRENCY, primera.get(2).getFormat());
    }
}