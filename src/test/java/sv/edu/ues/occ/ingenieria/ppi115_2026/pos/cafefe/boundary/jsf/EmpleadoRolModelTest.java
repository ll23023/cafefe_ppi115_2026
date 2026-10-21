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
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRepository;
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
class EmpleadoRolModelTest {

    @Mock
    private EmpleadoRolRepository empleadoRolRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private EmpleadoRolModel modelo;

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

    private Rol rolActivo() {
        Rol r = new Rol(UUID.randomUUID());
        r.setActivo(true);
        return r;
    }

    private EmpleadoRol asignacion(Empleado empleado, Rol rol) {
        EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
        er.setIdEmpleado(empleado);
        er.setIdRol(rol);
        return er;
    }

    // Inicialización y listas

    @Test
    void getRepository_devuelveElInyectado() {
        assertSame(empleadoRolRepository, modelo.getRepository());
    }

    @Test
    void init_cargaRegistrosEmpleadosYRoles() {
        List<Empleado> empleados = List.of(new Empleado(UUID.randomUUID()));
        List<Rol> roles = List.of(new Rol(UUID.randomUUID()));
        when(empleadoRepository.findAll()).thenReturn(empleados);
        when(rolRepository.findAll()).thenReturn(roles);

        modelo.init();

        assertSame(empleados, modelo.getEmpleados());
        assertSame(roles, modelo.getRoles());
        verify(empleadoRolRepository, times(1)).findAll();
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    @Test
    void init_sinRepositorios_dejaTodasLasListasVacias() {
        EmpleadoRolModel sinRepositorios = new EmpleadoRolModel();

        sinRepositorios.init();

        assertTrue(sinRepositorios.getRegistros().isEmpty());
        assertTrue(sinRepositorios.getEmpleados().isEmpty());
        assertTrue(sinRepositorios.getRoles().isEmpty());
    }

    @Test
    void settersDeEmpleadosYRoles_reemplazanLasListas() {
        List<Empleado> empleados = List.of();
        List<Rol> roles = List.of();

        modelo.setEmpleados(empleados);
        modelo.setRoles(roles);

        assertSame(empleados, modelo.getEmpleados());
        assertSame(roles, modelo.getRoles());
    }

    // Métodos abstractos implementados

    @Test
    void instanciarRegistro_creaUnaAsignacionActiva() {
        EmpleadoRol er = modelo.instanciarRegistro();

        assertNotNull(er.getIdEmpleadoRol());
        assertEquals(Boolean.TRUE, er.getActivo());
        assertEquals("", er.getObservaciones());
    }

    @Test
    void getRegistroById_conNulo_devuelveNulo() {
        assertNull(modelo.getRegistroById(null));
        verifyNoInteractions(empleadoRolRepository);
    }

    @Test
    void getRegistroById_conId_buscaEnElRepositorio() {
        UUID id = UUID.randomUUID();
        EmpleadoRol er = new EmpleadoRol(id);
        when(empleadoRolRepository.findById(id)).thenReturn(er);

        assertSame(er, modelo.getRegistroById(id));
    }

    @Test
    void getIdByRegistro_devuelveElIdOnuloSiNoHayRegistro() {
        UUID id = UUID.randomUUID();

        assertEquals(id, modelo.getIdByRegistro(new EmpleadoRol(id)));
        assertNull(modelo.getIdByRegistro(null));
    }

    @Test
    void getColumnas_devuelveLasSeisColumnas() {
        List<ColumnaTabla> columnas = modelo.getColumnas();

        assertEquals(6, columnas.size());
        assertEquals("idRol.nombre", columnas.get(3).getProperty());
        assertEquals(FORMATO_COLUMNA.BOOLEAN, columnas.get(4).getFormat());
    }

    @Test
    void btnGuardarHandler_sinRegistro_noHaceNada() {
        modelo.setRegistro(null);

        modelo.btnGuardarHandler();

        verifyNoInteractions(empleadoRolRepository, contexto);
    }

    @Test
    void btnGuardarHandler_enModificar_restauraEmpleadoYRolDesdeLaBase() {
        UUID id = UUID.randomUUID();
        Empleado empleadoOriginal = new Empleado(UUID.randomUUID());
        Rol rolOriginal = new Rol(UUID.randomUUID());
        EmpleadoRol original = asignacion(empleadoOriginal, rolOriginal);
        original.setIdEmpleadoRol(id);
        EmpleadoRol editado = asignacion(new Empleado(UUID.randomUUID()), new Rol(UUID.randomUUID()));
        editado.setIdEmpleadoRol(id);
        modelo.setRegistro(editado);
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);
        when(empleadoRolRepository.findById(id)).thenReturn(original);

        modelo.btnGuardarHandler();

        assertSame(empleadoOriginal, editado.getIdEmpleado());
        assertSame(rolOriginal, editado.getIdRol());
        verify(empleadoRolRepository, times(1)).modificar(editado);
        verify(empleadoRolRepository, never()).crear(any());
    }

    @Test
    void btnGuardarHandler_enModificarSinOriginal_modificaTalCual() {
        UUID id = UUID.randomUUID();
        Empleado empleado = new Empleado(UUID.randomUUID());
        EmpleadoRol editado = asignacion(empleado, new Rol(UUID.randomUUID()));
        editado.setIdEmpleadoRol(id);
        modelo.setRegistro(editado);
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.btnGuardarHandler();

        assertSame(empleado, editado.getIdEmpleado());
        verify(empleadoRolRepository, times(1)).modificar(editado);
    }

    @Test
    void btnGuardarHandler_conRolInactivo_avisaYNoGuarda() {
        modelo.limpiar();
        EmpleadoRol nueva = modelo.getRegistro();
        Rol inactivo = new Rol(UUID.randomUUID());
        inactivo.setActivo(false);
        nueva.setIdEmpleado(new Empleado(UUID.randomUUID()));
        nueva.setIdRol(inactivo);

        modelo.btnGuardarHandler();

        FacesMessage mensaje = unicoMensaje();
        assertEquals("No se puede asignar un rol inactivo.", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getSeverity());
        verify(empleadoRolRepository, never()).crear(any());
    }

    @Test
    void btnGuardarHandler_conRolSinEstado_loTratanComoInactivo() {
        modelo.limpiar();
        EmpleadoRol nueva = modelo.getRegistro();
        nueva.setIdRol(new Rol(UUID.randomUUID()));

        modelo.btnGuardarHandler();

        assertEquals("No se puede asignar un rol inactivo.", unicoMensaje().getSummary());
        verify(empleadoRolRepository, never()).crear(any());
    }

    @Test
    void btnGuardarHandler_asignacionDuplicada_avisaYNoGuarda() {
        modelo.limpiar();
        EmpleadoRol nueva = modelo.getRegistro();
        Empleado empleado = new Empleado(UUID.randomUUID());
        Rol rol = rolActivo();
        nueva.setIdEmpleado(empleado);
        nueva.setIdRol(rol);
        when(empleadoRolRepository.findAll()).thenReturn(List.of(asignacion(empleado, rol)));

        modelo.btnGuardarHandler();

        FacesMessage mensaje = unicoMensaje();
        assertEquals("Ese empleado ya tiene asignado ese rol.", mensaje.getSummary());
        assertEquals(FacesMessage.SEVERITY_ERROR, mensaje.getSeverity());
        verify(empleadoRolRepository, never()).crear(any());
    }

    @Test
    void btnGuardarHandler_sinCoincidenciasEnLasAsignaciones_crea() {
        modelo.limpiar();
        EmpleadoRol nueva = modelo.getRegistro();
        Empleado empleado = new Empleado(UUID.randomUUID());
        Rol rol = rolActivo();
        nueva.setIdEmpleado(empleado);
        nueva.setIdRol(rol);
        when(empleadoRolRepository.findAll()).thenReturn(List.of(
                asignacion(null, rol),
                asignacion(empleado, null),
                asignacion(empleado, rolActivo()),
                asignacion(new Empleado(UUID.randomUUID()), rol)));

        modelo.btnGuardarHandler();

        verify(empleadoRolRepository, times(1)).crear(nueva);
        verifyNoInteractions(contexto);
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
        assertNotSame(nueva, modelo.getRegistro());
    }

    @Test
    void btnGuardarHandler_sinEmpleadoNiRol_creaSinRevisarDuplicados() {
        modelo.limpiar();
        EmpleadoRol nueva = modelo.getRegistro();

        modelo.btnGuardarHandler();

        verify(empleadoRolRepository, times(1)).crear(nueva);
        verifyNoInteractions(contexto);
    }

    @Test
    void btnGuardarHandler_conEmpleadoPeroSinRol_creaSinRevisarDuplicados() {
        modelo.limpiar();
        EmpleadoRol nueva = modelo.getRegistro();
        nueva.setIdEmpleado(new Empleado(UUID.randomUUID()));

        modelo.btnGuardarHandler();

        verify(empleadoRolRepository, times(1)).crear(nueva);
    }
}