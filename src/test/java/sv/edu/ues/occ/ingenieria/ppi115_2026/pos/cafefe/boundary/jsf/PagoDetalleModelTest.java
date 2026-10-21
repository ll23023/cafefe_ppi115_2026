package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoDetalleRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.PagoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Pago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.PagoDetalle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class PagoDetalleModelTest {

    @Mock
    private PagoDetalleRepository pagoDetalleRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoDetalleModel modelo;

    @Test
    void getRepository_devuelveElInyectado() {
        assertSame(pagoDetalleRepository, modelo.getRepository());
    }

    @Test
    void instanciarRegistro_creaUnDetalleConId() {
        PagoDetalle p = modelo.instanciarRegistro();

        assertNotNull(p.getIdPagoDetalle());
        assertEquals("", p.getObservaciones());
        assertEquals("", p.getReferenciaExterna());
    }

    @Test
    void getRegistroById_conNulo_devuelveNulo() {
        assertNull(modelo.getRegistroById(null));
        verifyNoInteractions(pagoDetalleRepository);
    }

    @Test
    void getRegistroById_conId_buscaEnElRepositorio() {
        UUID id = UUID.randomUUID();
        PagoDetalle detalle = new PagoDetalle(id);
        when(pagoDetalleRepository.findById(id)).thenReturn(detalle);

        assertSame(detalle, modelo.getRegistroById(id));
    }

    @Test
    void getIdByRegistro_devuelveElIdOnuloSiNoHayRegistro() {
        UUID id = UUID.randomUUID();

        assertEquals(id, modelo.getIdByRegistro(new PagoDetalle(id)));
        assertNull(modelo.getIdByRegistro(null));
    }

    @Test
    void getTiposPagoDisponibles_devuelveLosTresTipos() {
        assertEquals(List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA"),
                modelo.getTiposPagoDisponibles());
    }

    @Test
    void getPagos_cargaPerezosaUnaSolaVez() {
        List<Pago> pagos = new ArrayList<>();
        when(pagoRepository.findAll()).thenReturn(pagos);

        List<Pago> primera = modelo.getPagos();
        List<Pago> segunda = modelo.getPagos();

        assertSame(pagos, primera);
        assertSame(primera, segunda);
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void getPagos_sinRepositorio_devuelveListaVacia() {
        PagoDetalleModel sinRepositorios = new PagoDetalleModel();

        assertTrue(sinRepositorios.getPagos().isEmpty());
    }

    @Test
    void setPagos_reemplazaLaLista() {
        List<Pago> pagos = new ArrayList<>();

        modelo.setPagos(pagos);

        assertSame(pagos, modelo.getPagos());
        verifyNoInteractions(pagoRepository);
    }

    @Test
    void getColumnas_creaLaListaUnaSolaVez() {
        List<ColumnaTabla> primera = modelo.getColumnas();
        List<ColumnaTabla> segunda = modelo.getColumnas();

        assertEquals(5, primera.size());
        assertSame(primera, segunda);
        assertEquals("monto", primera.get(1).getProperty());
        assertEquals(FORMATO_COLUMNA.CURRENCY, primera.get(1).getFormat());
    }
}