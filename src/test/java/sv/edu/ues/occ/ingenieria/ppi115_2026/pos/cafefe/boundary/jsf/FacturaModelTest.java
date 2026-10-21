package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CajaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.FacturaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caja;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Factura;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class FacturaModelTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private EmpleadoRolRepository empleadoRolRepository;

    @Mock
    private CajaRepository cajaRepository;

    @InjectMocks
    private FacturaModel modelo;

    // Métodos abstractos implementados

    @Test
    void getRepository_devuelveElInyectado() {
        assertSame(facturaRepository, modelo.getRepository());
    }

    @Test
    void instanciarRegistro_creaUnaFacturaPendienteConFecha() {
        Factura f = modelo.instanciarRegistro();

        assertNotNull(f.getIdFactura());
        assertNotNull(f.getFechaFacturacion());
        assertEquals("PENDIENTE", f.getEstado());
        assertEquals("", f.getObservaciones());
    }

    @Test
    void getRegistroById_conNulo_devuelveNulo() {
        assertNull(modelo.getRegistroById(null));
        verifyNoInteractions(facturaRepository);
    }

    @Test
    void getRegistroById_conId_buscaEnElRepositorio() {
        UUID id = UUID.randomUUID();
        Factura f = new Factura(id);
        when(facturaRepository.findById(id)).thenReturn(f);

        assertSame(f, modelo.getRegistroById(id));
    }

    @Test
    void getIdByRegistro_devuelveElIdOnuloSiNoHayRegistro() {
        UUID id = UUID.randomUUID();

        assertEquals(id, modelo.getIdByRegistro(new Factura(id)));
        assertNull(modelo.getIdByRegistro(null));
    }

    // limpiar y seleccionarRegistro

    @Test
    void limpiar_fijaFechaYHoraDeAhora() {
        modelo.limpiar();

        assertNotNull(modelo.getFechaSeleccionada());
        assertSame(modelo.getFechaSeleccionada(), modelo.getHoraSeleccionada());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void seleccionarRegistro_conFecha_actualizaFechaYHora() {
        Factura f = new Factura(UUID.randomUUID());
        f.setFechaFacturacion(new Date());

        modelo.seleccionarRegistro(f);

        assertSame(f, modelo.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, modelo.getEstado());
        assertSame(f.getFechaFacturacion(), modelo.getFechaSeleccionada());
        assertSame(f.getFechaFacturacion(), modelo.getHoraSeleccionada());
    }

    @Test
    void seleccionarRegistro_sinFecha_noCambiaLasFechas() {
        modelo.limpiar();
        Date antes = modelo.getFechaSeleccionada();
        Factura f = new Factura(UUID.randomUUID());

        modelo.seleccionarRegistro(f);

        assertSame(f, modelo.getRegistro());
        assertSame(antes, modelo.getFechaSeleccionada());
    }

    @Test
    void seleccionarRegistro_conNulo_noCambiaNada() {
        modelo.seleccionarRegistro(null);

        assertNull(modelo.getRegistro());
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
        assertNull(modelo.getFechaSeleccionada());
    }


    @Test
    void btnGuardarHandler_combinaLaFechaConLaHoraSinSegundos() {
        modelo.limpiar();
        Factura f = modelo.getRegistro();
        Calendar cFecha = Calendar.getInstance();
        cFecha.clear();
        cFecha.set(2026, Calendar.OCTOBER, 20, 8, 0, 0);
        Calendar cHora = Calendar.getInstance();
        cHora.clear();
        cHora.set(2000, Calendar.JANUARY, 1, 14, 35, 50);
        cHora.set(Calendar.MILLISECOND, 123);
        modelo.setFechaSeleccionada(cFecha.getTime());
        modelo.setHoraSeleccionada(cHora.getTime());

        modelo.btnGuardarHandler();

        verify(facturaRepository, times(1)).crear(f);
        Calendar resultado = Calendar.getInstance();
        resultado.setTime(f.getFechaFacturacion());
        assertEquals(2026, resultado.get(Calendar.YEAR));
        assertEquals(Calendar.OCTOBER, resultado.get(Calendar.MONTH));
        assertEquals(20, resultado.get(Calendar.DAY_OF_MONTH));
        assertEquals(14, resultado.get(Calendar.HOUR_OF_DAY));
        assertEquals(35, resultado.get(Calendar.MINUTE));
        assertEquals(0, resultado.get(Calendar.SECOND));
        assertEquals(0, resultado.get(Calendar.MILLISECOND));
    }

    @Test
    void btnGuardarHandler_sinFechaNiHora_usaElMomentoActual() {
        modelo.limpiar();
        Factura f = modelo.getRegistro();
        modelo.setFechaSeleccionada(null);
        modelo.setHoraSeleccionada(null);

        modelo.btnGuardarHandler();

        assertNotNull(f.getFechaFacturacion());
        verify(facturaRepository, times(1)).crear(f);
    }

    @Test
    void btnGuardarHandler_sinRegistro_noHaceNada() {
        modelo.setRegistro(null);

        modelo.btnGuardarHandler();

        verifyNoInteractions(facturaRepository);
    }

    // Listas para los combos

    @Test
    void getEstadosDisponibles_devuelvePendienteYCancelado() {
        assertEquals(List.of("PENDIENTE", "CANCELADO"), modelo.getEstadosDisponibles());
    }

    @Test
    void getEmpleadoRoles_cargaPerezosaUnaSolaVez() {
        List<EmpleadoRol> lista = new ArrayList<>();
        when(empleadoRolRepository.findAll()).thenReturn(lista);

        List<EmpleadoRol> primera = modelo.getEmpleadoRoles();
        List<EmpleadoRol> segunda = modelo.getEmpleadoRoles();

        assertSame(lista, primera);
        assertSame(primera, segunda);
        verify(empleadoRolRepository, times(1)).findAll();
    }

    @Test
    void getEmpleadoRoles_sinRepositorio_devuelveListaVacia() {
        assertTrue(new FacturaModel().getEmpleadoRoles().isEmpty());
    }

    @Test
    void setEmpleadoRoles_reemplazaLaLista() {
        List<EmpleadoRol> lista = new ArrayList<>();

        modelo.setEmpleadoRoles(lista);

        assertSame(lista, modelo.getEmpleadoRoles());
        verifyNoInteractions(empleadoRolRepository);
    }

    @Test
    void getCajas_cargaPerezosaUnaSolaVez() {
        List<Caja> lista = new ArrayList<>();
        when(cajaRepository.findAll()).thenReturn(lista);

        List<Caja> primera = modelo.getCajas();
        List<Caja> segunda = modelo.getCajas();

        assertSame(lista, primera);
        assertSame(primera, segunda);
        verify(cajaRepository, times(1)).findAll();
    }

    @Test
    void getCajas_sinRepositorio_devuelveListaVacia() {
        assertTrue(new FacturaModel().getCajas().isEmpty());
    }

    @Test
    void setCajas_reemplazaLaLista() {
        List<Caja> lista = new ArrayList<>();

        modelo.setCajas(lista);

        assertSame(lista, modelo.getCajas());
        verifyNoInteractions(cajaRepository);
    }

    @Test
    void getColumnas_creaLaListaUnaSolaVez() {
        List<ColumnaTabla> primera = modelo.getColumnas();
        List<ColumnaTabla> segunda = modelo.getColumnas();

        assertEquals(6, primera.size());
        assertSame(primera, segunda);
        assertEquals(FORMATO_COLUMNA.DATE, primera.get(1).getFormat());
    }
}