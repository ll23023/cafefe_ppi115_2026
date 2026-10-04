package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Descuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento;

/**
 *
 * @author mil
 */
@Named("tipoDescuentoModel")
@ViewScoped
public class TipoDescuentoModel extends DefaultModel<TipoDescuento, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(TipoDescuentoModel.class.getName());
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final int DESCUENTO_MAXIMO_MIN = 5;
    private static final int DESCUENTO_MAXIMO_MAX = 90;

    @Inject
    private TipoDescuentoRepository tipoDescuentoRepository;

    @Inject
    private DescuentoRepository descuentoRepository;

    private boolean soloLectura = false;
    private List<Descuento> descuentos = new ArrayList<>();
    private List<Descuento> descuentosDelTipo = new ArrayList<>();

    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("descuentoMaximo", "Máximo (%)"),
            new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN)
    );

    @Override
    public TipoDescuento instanciarRegistro() {
        TipoDescuento r = new TipoDescuento(UUID.randomUUID());
        r.setActivo(Boolean.TRUE);
        r.setObservaciones("");
        r.setDescuentoMaximo(DESCUENTO_MAXIMO_MIN);
        return r;
    }

    @Override
    public TipoDescuento getRegistroById(Object id) {
        if (id != null && this.registros != null && !this.registros.isEmpty()) {
            UUID busca = (UUID) id;
            return this.registros.stream()
                    .filter(r -> r.getIdTipoDescuento().equals(busca))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Object getIdByRegistro(TipoDescuento dato) {
        if (dato != null) {
            return dato.getIdTipoDescuento();
        }
        return null;
    }

    @Override
    public TipoDescuentoRepository getRepository() {
        return tipoDescuentoRepository;
    }

    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        this.descuentos = descuentoRepository != null ? descuentoRepository.findAll() : new ArrayList<>();
        refrescarDetalle();
    }

    @Override
    public void limpiar() {
        super.limpiar();
        this.soloLectura = false;
        refrescarDetalle();  
    }

    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(TipoDescuento t) {
        if (t == null) {
            return;
        }
        this.seleccion = t;
        mostrar(t);
        this.soloLectura = false;
        this.estado = ESTADO_CRUD.MODIFICAR;
    }

    public void cancelar() {
        if (estado == ESTADO_CRUD.MODIFICAR && seleccion != null) {
            mostrar(seleccion);
        } else {
            limpiar();
        }
    }

    // Solo se elimina un tipo que no tiene descuentos (hijos). 
    public void eliminar(TipoDescuento t) {
        if (t == null) {
            return;
        }
        try {
            int hijos = contar(t.getIdTipoDescuento(), todosLosDescuentos());
            if (hijos > 0) {
                error("No se puede eliminar el tipo \"" + t.getNombre() + "\": tiene " + hijos
                        + (hijos == 1 ? " descuento asociado." : " descuentos asociados."));
                return;
            }
            tipoDescuentoRepository.eliminar(t);
            cargarRegistros();
            limpiar();
            info("Tipo de descuento eliminado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar tipo de descuento", ex);
            error("No se pudo eliminar el tipo de descuento: " + ex.getMessage());
        }
    }

    @Override
    public void btnGuardarHandler() {
        if (soloLectura || registro == null) {
            return;
        }
        String nombre = registro.getNombre() == null ? "" : registro.getNombre().trim();
        if (nombre.isEmpty()) {
            error("Indique el nombre del tipo de descuento.");
            return;
        }
        registro.setNombre(nombre);

        Integer maximo = aEntero(registro.getDescuentoMaximo());
        if (maximo == null || maximo < DESCUENTO_MAXIMO_MIN || maximo > DESCUENTO_MAXIMO_MAX) {
            error("El descuento máximo debe estar entre " + DESCUENTO_MAXIMO_MIN
                    + "% y " + DESCUENTO_MAXIMO_MAX + "%.");
            return;
        }
        if (registro.getObservaciones() == null) {
            registro.setObservaciones("");
        }
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }

        boolean esNuevo = isCreando();
        try {
            if (esNuevo) {
                tipoDescuentoRepository.crear(registro);
            } else {
                tipoDescuentoRepository.modificar(registro);
            }
            UUID id = registro.getIdTipoDescuento();
            cargarRegistros();
            TipoDescuento guardado = registros.stream()
                    .filter(t -> id.equals(t.getIdTipoDescuento()))
                    .findFirst().orElse(null);
            if (guardado != null) {
                this.seleccion = guardado;
                mostrar(guardado);
            } else {
                limpiar();
            }
            info(esNuevo ? "Tipo de descuento creado." : "Tipo de descuento modificado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar tipo de descuento", ex);
            error("No se pudo guardar el tipo de descuento: " + ex.getMessage());
        }
    }

    private void mostrar(TipoDescuento t) {
        TipoDescuento fresco = t.getIdTipoDescuento() == null
                ? null : tipoDescuentoRepository.findById(t.getIdTipoDescuento());
        this.registro = fresco != null ? fresco : t;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;
        refrescarDetalle();
    }

    private void refrescarDetalle() {
        List<Descuento> lista = new ArrayList<>();
        if (registro != null && registro.getIdTipoDescuento() != null && descuentos != null) {
            UUID idTipo = registro.getIdTipoDescuento();
            for (Descuento d : descuentos) {
                if (perteneceAlTipo(d, idTipo)) {
                    lista.add(d);
                }
            }
            lista.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                    a.getNombre() == null ? "" : a.getNombre(),
                    b.getNombre() == null ? "" : b.getNombre()));
        }
        this.descuentosDelTipo = lista;
    }

    public int cantidadDescuentos(TipoDescuento t) {
        return t == null ? 0 : contar(t.getIdTipoDescuento(), descuentos);
    }

    private List<Descuento> todosLosDescuentos() {
        return descuentoRepository != null ? descuentoRepository.findAll() : new ArrayList<>();
    }

    private static int contar(UUID idTipo, List<Descuento> fuente) {
        if (idTipo == null || fuente == null) {
            return 0;
        }
        int n = 0;
        for (Descuento d : fuente) {
            if (perteneceAlTipo(d, idTipo)) {
                n++;
            }
        }
        return n;
    }

    private static boolean perteneceAlTipo(Descuento d, UUID idTipo) {
        return d != null
                && d.getIdTipoDescuento() != null
                && idTipo.equals(d.getIdTipoDescuento().getIdTipoDescuento());
    }

    private static Integer aEntero(Object o) {
        return o instanceof Number n ? n.intValue() : null;
    }

    public String fechaTexto(Object fecha) {
        LocalDateTime f = aLocalDateTime(fecha);
        return f == null ? "" : f.format(FORMATO_FECHA_HORA);
    }

    public String fechaHastaTexto(Object fecha) {
        LocalDateTime f = aLocalDateTime(fecha);
        return f == null ? "Sin fecha de finalización" : f.format(FORMATO_FECHA_HORA);
    }

    private static LocalDateTime aLocalDateTime(Object o) {
        if (o == null) {
            return null;
        }
        ZoneId zona = ZoneId.systemDefault();
        if (o instanceof LocalDateTime ldt) {
            return ldt;
        }
        if (o instanceof LocalDate ld) {
            return ld.atStartOfDay();
        }
        if (o instanceof OffsetDateTime odt) {
            return odt.atZoneSameInstant(zona).toLocalDateTime();
        }
        if (o instanceof ZonedDateTime zdt) {
            return zdt.withZoneSameInstant(zona).toLocalDateTime();
        }
        if (o instanceof Instant i) {
            return LocalDateTime.ofInstant(i, zona);
        }
        if (o instanceof java.sql.Date sd) {
            return sd.toLocalDate().atStartOfDay();
        }
        if (o instanceof Date d) {
            return LocalDateTime.ofInstant(d.toInstant(), zona);
        }
        return null;
    }

    private void info(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public boolean isSoloLectura() {
        return soloLectura;
    }

    public String getTituloFormulario() {
        if (soloLectura) {
            return "Detalle del tipo de descuento";
        }
        return isEditando() ? "Editar tipo de descuento" : "Nuevo tipo de descuento";
    }

    public List<Descuento> getDescuentosDelTipo() {
        return descuentosDelTipo;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }

    public List<Integer> getOpcionesDescuento() {
        List<Integer> opciones = new ArrayList<>();
        for (int i = DESCUENTO_MAXIMO_MIN; i <= DESCUENTO_MAXIMO_MAX; i += 5) {
            opciones.add(i);
        }
        return opciones;
    }
}