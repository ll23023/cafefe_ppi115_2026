package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.RolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Rol;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 
 * @author mil
 */
@ExtendWith(MockitoExtension.class)
class RolModelTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private EmpleadoRolRepository empleadoRolRepository;

    @InjectMocks
    private RolModel modelo;

    private FacesContext contexto;
    private MockedStatic<FacesContext> estatico;

    @BeforeEach
    void setUp() {
        contexto = mock(FacesContext.class);
        estatico = mockStatic(FacesContext.class, withSettings().lenient());
        estatico.when(FacesContext::getCurrentInstance).thenReturn(contexto);
    }

    @AfterEach
    void tearDown() {
        estatico.close();
    }

    private FacesMessage unicoMensaje() {
        ArgumentCaptor<FacesMessage> captor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(contexto, times(1)).addMessage(isNull(), captor.capture());
        return captor.getValue();
    }

    private Empleado empleado(String nombre) {
        Empleado e = new Empleado(UUID.randomUUID());
        e.setNombre(nombre);
        return e;
    }

    private EmpleadoRol asignacion(Rol rol, Empleado empleado) {
        EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
        er.setIdRol(rol);
        er.setIdEmpleado(empleado);
        return er;
    }

    // Métodos abstractos implementados

    @Test
    void getRepository_devuelveElInyectado() {
        assertSame(rolRepository, modelo.getRepository());
    }

    @Test
    void instanciarRegistro_creaUnRolActivoConId() {
        Rol r = modelo.instanciarRegistro();

        assertNotNull(r.getIdRol());
        assertEquals(Boolean.TRUE, r.getActivo());
        assertEquals("", r.getObservaciones());
    }

    @Test
    void getRegistroById_conNulo_devuelveNulo() {
        assertNull(modelo.getRegistroById(null));
        verifyNoInteractions(rolRepository);
    }

    @Test
    void getRegistroById_conId_buscaEnElRepositorio() {
        UUID id = UUID.randomUUID();
        Rol rol = new Rol(id);
        when(rolRepository.findById(id)).thenReturn(rol);

        assertSame(rol, modelo.getRegistroById(id));
    }

    @Test
    void getIdByRegistro_devuelveElIdOnuloSiNoHayRegistro() {
        UUID id = UUID.randomUUID();

        assertEquals(id, modelo.getIdByRegistro(new Rol(id)));
        assertNull(modelo.getIdByRegistro(null));
    }

    @Test
    void limpiar_reiniciaElFormulario() {
        modelo.limpiar();

        assertNotNull(modelo.getRegistro());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
        assertFalse(modelo.isSoloLectura());
        assertTrue(modelo.getEmpleadosDelRol().isEmpty());
    }

    @Test
    void onSeleccionar_sinSeleccion_noHaceNada() {
        modelo.onSeleccionar();

        verifyNoInteractions(rolRepository, empleadoRolRepository);
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
    }

    @Test
    void onSeleccionar_conSeleccion_muestraElRolYSusEmpleadosOrdenados() {
        UUID id = UUID.randomUUID();
        Rol seleccionado = new Rol(id);
        Rol fresco = new Rol(id);
        Rol otroRol = new Rol(UUID.randomUUID());
        Empleado zoe = empleado("Zoe");
        Empleado ana = empleado("ana");
        Empleado sinNombre = empleado(null);
        when(rolRepository.findById(id)).thenReturn(fresco);
        when(empleadoRolRepository.findAll()).thenReturn(List.of(
                asignacion(fresco, zoe),
                asignacion(fresco, ana),
                asignacion(fresco, zoe),
                asignacion(fresco, sinNombre),
                asignacion(otroRol, ana),
                asignacion(null, ana),
                asignacion(fresco, null)));
        modelo.setSeleccion(seleccionado);

        modelo.onSeleccionar();

        assertSame(fresco, modelo.getRegistro());
        assertTrue(modelo.isSoloLectura());
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
        List<Empleado> empleados = modelo.getEmpleadosDelRol();
        assertEquals(3, empleados.size());
        assertSame(sinNombre, empleados.get(0));
        assertSame(ana, empleados.get(1));
        assertSame(zoe, empleados.get(2));
    }

    @Test
    void onSeleccionar_siElRolYaNoExiste_usaElDeLaSeleccion() {
        Rol seleccionado = new Rol(UUID.randomUUID());
        when(rolRepository.findById(seleccionado.getIdRol())).thenReturn(null);
        modelo.setSeleccion(seleccionado);

        modelo.onSeleccionar();

        assertSame(seleccionado, modelo.getRegistro());
    }

    @Test
    void editar_dejaElFormularioEditable() {
        Rol rol = new Rol(UUID.randomUUID());

        modelo.editar(rol);

        assertSame(rol, modelo.getSeleccion());
        assertEquals(ESTADO_CRUD.MODIFICAR, modelo.getEstado());
        assertFalse(modelo.isSoloLectura());
    }

    @Test
    void cancelar_limpiaElFormulario() {
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.cancelar();

        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void eliminar_conNulo_noHaceNada() {
        modelo.eliminar(null);

        verifyNoInteractions(rolRepository, empleadoRolRepository, contexto);
    }

    @Test
    void eliminar_conEmpleadosAsignados_noEliminaYAvisa() {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Mesero");
        when(empleadoRolRepository.findAll())
                .thenReturn(List.of(asignacion(rol, empleado("Ana"))));

        modelo.eliminar(rol);

        FacesMessage mensaje = unicoMensaje();
        assertEquals("No se puede eliminar \"Mesero\": lo tienen 1 empleado(s). "
                + "Puede marcarlo como inactivo en su lugar.", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getSeverity());
        verify(rolRepository, never()).eliminar(any());
    }

    @Test
    void eliminar_sinEmpleados_eliminaYAvisa() {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Mesero");

        modelo.eliminar(rol);

        verify(rolRepository, times(1)).eliminar(rol);
        FacesMessage mensaje = unicoMensaje();
        assertEquals("Rol eliminado.", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_INFO, mensaje.getSeverity());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void eliminar_cuandoFalla_avisaDelError() {
        Rol rol = new Rol(UUID.randomUUID());
        when(empleadoRolRepository.findAll()).thenThrow(new RuntimeException("boom"));

        modelo.eliminar(rol);

        FacesMessage mensaje = unicoMensaje();
        assertEquals("No se pudo eliminar el rol: boom", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getSeverity());
    }

    @Test
    void btnGuardarHandler_enSoloLectura_noHaceNada() {
        Rol rol = new Rol(UUID.randomUUID());
        modelo.setSeleccion(rol);
        modelo.onSeleccionar();

        modelo.btnGuardarHandler();

        verify(rolRepository, never()).crear(any());
        verify(rolRepository, never()).modificar(any());
        verifyNoInteractions(contexto);
    }

    @Test
    void btnGuardarHandler_sinRegistro_noHaceNada() {
        modelo.setRegistro(null);

        modelo.btnGuardarHandler();

        verifyNoInteractions(rolRepository);
    }

    @Test
    void btnGuardarHandler_nombreRepetido_avisaYNoGuarda() {
        modelo.limpiar();
        Rol registro = modelo.getRegistro();
        registro.setNombre("  Cajero ");
        Rol existente = new Rol(UUID.randomUUID());
        existente.setNombre("cajero");
        Rol sinNombre = new Rol(UUID.randomUUID());
        when(rolRepository.findAll()).thenReturn(List.of(sinNombre, existente));

        modelo.btnGuardarHandler();

        assertEquals("Cajero", registro.getNombre());
        FacesMessage mensaje = unicoMensaje();
        assertEquals("Ya existe un rol llamado \"Cajero\".", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getSeverity());
        verify(rolRepository, never()).crear(any());
        verify(rolRepository, never()).modificar(any());
    }

    @Test
    void btnGuardarHandler_nuevo_creaYLimpia() {
        modelo.limpiar();
        Rol registro = modelo.getRegistro();
        registro.setNombre("Mesero");

        modelo.btnGuardarHandler();

        verify(rolRepository, times(1)).crear(registro);
        verify(rolRepository, never()).modificar(any());
        FacesMessage mensaje = unicoMensaje();
        assertEquals("Rol guardado.", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_INFO, mensaje.getSeverity());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
        assertNotSame(registro, modelo.getRegistro());
    }

    @Test
    void btnGuardarHandler_conNombreNulo_loNormalizaAVacio() {
        modelo.limpiar();
        Rol registro = modelo.getRegistro();
        registro.setNombre(null);

        modelo.btnGuardarHandler();

        assertEquals("", registro.getNombre());
        verify(rolRepository, times(1)).crear(registro);
    }

    @Test
    void btnGuardarHandler_enModificar_modificaSinChocarConSiMismo() {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Cajero");
        modelo.setRegistro(rol);
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);
        when(rolRepository.findAll()).thenReturn(List.of(rol));

        modelo.btnGuardarHandler();

        verify(rolRepository, times(1)).modificar(rol);
        verify(rolRepository, never()).crear(any());
        assertEquals("Rol guardado.", unicoMensaje().getSummary());
    }

    @Test
    void btnGuardarHandler_cuandoFalla_avisaDelError() {
        modelo.limpiar();
        modelo.getRegistro().setNombre("Mesero");
        doThrow(new RuntimeException("boom")).when(rolRepository).crear(any());

        modelo.btnGuardarHandler();

        FacesMessage mensaje = unicoMensaje();
        assertEquals("No se pudo guardar el rol: boom", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getSeverity());
    }

    // Utilidades de vista

    @Test
    void getTituloFormulario_segunElEstado() {
        assertEquals("Nuevo rol", modelo.getTituloFormulario());

        modelo.setEstado(ESTADO_CRUD.MODIFICAR);
        assertEquals("Editar rol", modelo.getTituloFormulario());

        modelo.setSeleccion(new Rol(UUID.randomUUID()));
        modelo.onSeleccionar();
        assertEquals("Detalle del rol", modelo.getTituloFormulario());
    }

    @Test
    void getColumnas_creaLaListaUnaSolaVez() {
        List<ColumnaTabla> primera = modelo.getColumnas();
        List<ColumnaTabla> segunda = modelo.getColumnas();

        assertEquals(3, primera.size());
        assertSame(primera, segunda);
        assertEquals("nombre", primera.get(0).getProperty());
        assertEquals(FORMATO_COLUMNA.BOOLEAN, primera.get(1).getFormat());
    }
}