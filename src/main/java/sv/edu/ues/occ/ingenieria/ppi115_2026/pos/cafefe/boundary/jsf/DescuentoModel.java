package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
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
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Descuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.DescuentoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento;

/**
 * @author mil
 */
@Named
@ViewScoped
public class DescuentoModel extends DefaultModel<Descuento, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(DescuentoModel.class.getName());
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Inject
    private DescuentoRepository descuentoRepository;
    @Inject
    private TipoDescuentoRepository tipoDescuentoRepository;
    @Inject
    private DescuentoProductoRepository descuentoProductoRepository;

    /** Maestro: todos los tipos de descuento. */
    private List<TipoDescuento> tiposDescuento = new ArrayList<>();
    /** Tipo elegido en la lista (maestro); null si no hay selección. */
    private TipoDescuento tipoSeleccionado;
    /** Todas las asignaciones descuento-producto (se filtran por tipo en el detalle). */
    private List<DescuentoProducto> asignaciones = new ArrayList<>();
    /**
     * Detalle: asignaciones (producto + descuento) de los descuentos del tipo
     * seleccionado. Se recalcula con refrescarDetalle().
     */
    private List<DescuentoProducto> productosDelTipo = new ArrayList<>();
    /**
     * Id (texto) del tipo elegido en el combo del formulario. Se usa un String
     * en lugar de la entidad para no depender de un converter de TipoDescuento.
     */
    private String idTipoSel;
    /** true = el descuento no tiene fecha de finalización (abierto). */
    private boolean sinFechaFin;

    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("idDescuento", "ID"),
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("idTipoDescuento.nombre", "Tipo"),
            new ColumnaTabla("fechaDesde", "Desde", FORMATO_COLUMNA.DATE),
            new ColumnaTabla("fechaHasta", "Hasta", FORMATO_COLUMNA.DATE),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    private final List<ColumnaTabla> columnasTipos = Arrays.asList(
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("descuentoMaximo", "Máximo (%)"),
            new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    // ------------------------------------------------------------------
    // Ciclo de vida / CRUD base
    // ------------------------------------------------------------------
    @Override
    public DescuentoRepository getRepository() {
        return descuentoRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        cargarTipos();   // primero los tipos: limpiar() los consulta
        super.init();    // cargarRegistros() + limpiar()
    }

    @Override
    public Descuento instanciarRegistro() {
        Descuento r = new Descuento(UUID.randomUUID());
        r.setObservaciones("");
        return r;
    }

    @Override
    public Descuento getRegistroById(Object id) {
        return id == null ? null : descuentoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Descuento dato) {
        return dato == null ? null : dato.getIdDescuento();
    }

    /** Recarga todos los descuentos y las asignaciones a productos, y recalcula el detalle. */
    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        this.asignaciones = descuentoProductoRepository != null
                ? descuentoProductoRepository.findAll()
                : new ArrayList<>();
        refrescarDetalle();
    }

    /**
     * Deja el formulario en modo creación. Si hay un tipo activo seleccionado
     * en la lista, queda preseleccionado en el combo (así se pueden crear
     * varios descuentos seguidos del mismo tipo).
     */
    @Override
    public void limpiar() {
        super.limpiar();
        this.sinFechaFin = false;
        this.idTipoSel = (tipoSeleccionado != null
                && Boolean.TRUE.equals(tipoSeleccionado.getActivo())
                && tipoSeleccionado.getIdTipoDescuento() != null)
                ? tipoSeleccionado.getIdTipoDescuento().toString()
                : null;
    }

    // ------------------------------------------------------------------
    // Maestro (tipos) y detalle (descuentos del tipo)
    // ------------------------------------------------------------------
    /** Se ejecuta al seleccionar o des-seleccionar una fila de la lista de tipos. */
    public void onSeleccionarTipo() {
        if (tipoSeleccionado != null
                && isCreando()
                && Boolean.TRUE.equals(tipoSeleccionado.getActivo())
                && tipoSeleccionado.getIdTipoDescuento() != null) {
            this.idTipoSel = tipoSeleccionado.getIdTipoDescuento().toString();
        }
        refrescarDetalle();
    }

    /** Cantidad de descuentos que tiene un tipo (columna de la lista de tipos). */
    public int cantidadDe(TipoDescuento tipo) {
        if (tipo == null || tipo.getIdTipoDescuento() == null || registros == null) {
            return 0;
        }
        int n = 0;
        for (Descuento d : registros) {
            if (perteneceAlTipo(d, tipo.getIdTipoDescuento())) {
                n++;
            }
        }
        return n;
    }

    private void cargarTipos() {
        if (tipoDescuentoRepository == null) {
            this.tiposDescuento = new ArrayList<>();
            return;
        }
        this.tiposDescuento = tipoDescuentoRepository.findAll().stream()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        a.getNombre() == null ? "" : a.getNombre(),
                        b.getNombre() == null ? "" : b.getNombre()))
                .toList();
    }

    /**
     * Arma el detalle: productos a los que se aplican los descuentos del tipo
     * seleccionado, ordenados por nombre de producto.
     */
    private void refrescarDetalle() {
        List<DescuentoProducto> lista = new ArrayList<>();
        if (tipoSeleccionado != null && tipoSeleccionado.getIdTipoDescuento() != null && asignaciones != null) {
            UUID idTipo = tipoSeleccionado.getIdTipoDescuento();
            for (DescuentoProducto dp : asignaciones) {
                if (dp != null && perteneceAlTipo(dp.getIdDescuento(), idTipo)) {
                    lista.add(dp);
                }
            }
            lista.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                    nombreProducto(a), nombreProducto(b)));
        }
        this.productosDelTipo = lista;
    }

    private static String nombreProducto(DescuentoProducto dp) {
        return dp.getIdProducto() == null || dp.getIdProducto().getNombre() == null
                ? "" : dp.getIdProducto().getNombre();
    }

    private static boolean perteneceAlTipo(Descuento d, UUID idTipo) {
        return d != null
                && d.getIdTipoDescuento() != null
                && idTipo.equals(d.getIdTipoDescuento().getIdTipoDescuento());
    }

    private TipoDescuento tipoPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (TipoDescuento t : tiposDescuento) {
            if (t.getIdTipoDescuento() != null && id.equals(t.getIdTipoDescuento().toString())) {
                return t;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Acciones del formulario y de la tabla de detalle
    // ------------------------------------------------------------------
    /** Carga un descuento del detalle en el formulario para modificarlo. */
    public void editar(Descuento d) {
        if (d == null) {
            return;
        }
        // Se recarga la lista para descartar cambios sin guardar de una edición
        // anterior (el formulario edita directamente el objeto de la lista).
        UUID id = d.getIdDescuento();
        cargarRegistros();
        Descuento fresco = registros.stream()
                .filter(x -> id != null && id.equals(x.getIdDescuento()))
                .findFirst()
                .orElse(d);
        seleccionarRegistro(fresco);   // registro = fresco, estado = MODIFICAR
        this.seleccion = fresco;
        this.idTipoSel = (fresco.getIdTipoDescuento() != null
                && fresco.getIdTipoDescuento().getIdTipoDescuento() != null)
                ? fresco.getIdTipoDescuento().getIdTipoDescuento().toString()
                : null;
        this.sinFechaFin = fresco.getFechaHasta() == null;
    }

    /** Botón "Nuevo" / "Cancelar": descarta lo no guardado y vuelve a modo creación. */
    public void cancelar() {
        cargarRegistros();
        limpiar();
    }

    public void eliminar(Descuento d) {
        if (d == null) {
            return;
        }
        try {
            descuentoRepository.eliminar(d);
            boolean eraElDelFormulario = registro != null && d.getIdDescuento() != null
                    && d.getIdDescuento().equals(registro.getIdDescuento());
            cargarRegistros();
            if (eraElDelFormulario) {
                limpiar();
            }
            info("Descuento eliminado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar descuento", ex);
            cargarRegistros();
            error("No se pudo eliminar el descuento. Verifique que no esté asignado a ningún producto.");
        }
    }

    /** Se ejecuta al marcar/desmarcar "Sin fecha de finalización". */
    public void onCambioSinFin() {
        if (sinFechaFin && registro != null) {
            registro.setFechaHasta(null);
        }
    }

    @Override
    public void btnGuardarHandler() {
        if (registro == null) {
            return;
        }
        boolean esNuevo = isCreando();

        // El tipo solo se elige al crear; al modificar se conserva el que ya tenía
        if (esNuevo) {
            TipoDescuento tipo = tipoPorId(idTipoSel);
            if (tipo == null) {
                error("Seleccione el tipo de descuento.");
                return;
            }
            if (!Boolean.TRUE.equals(tipo.getActivo())) {
                error("No se puede asignar un tipo de descuento inactivo: " + tipo.getNombre());
                return;
            }
            registro.setIdTipoDescuento(tipo);
        } else if (registro.getIdTipoDescuento() == null) {
            error("El descuento no tiene tipo asignado.");
            return;
        }

        String nombre = registro.getNombre() == null ? "" : registro.getNombre().trim();
        if (nombre.isEmpty()) {
            error("Indique el nombre del descuento.");
            return;
        }
        registro.setNombre(nombre);

        LocalDateTime desde = aLocalDateTime(registro.getFechaDesde());
        if (desde == null) {
            error("Indique la fecha de inicio del descuento.");
            return;
        }
        if (sinFechaFin) {
            registro.setFechaHasta(null);   // descuento abierto
        } else {
            LocalDateTime hasta = aLocalDateTime(registro.getFechaHasta());
            if (hasta == null) {
                error("Indique la fecha de finalización o marque \"Sin fecha de finalización\".");
                return;
            }
            if (hasta.isBefore(desde)) {
                error("La fecha de finalización no puede ser anterior a la fecha de inicio.");
                return;
            }
        }
        if (registro.getObservaciones() == null) {
            registro.setObservaciones("");
        }

        try {
            if (esNuevo) {
                getRepository().crear(registro);
            } else {
                getRepository().modificar(registro);
            }
            UUID idTipo = registro.getIdTipoDescuento().getIdTipoDescuento();
            cargarRegistros();
            // La lista de tipos se queda en el tipo del descuento guardado, para verlo en el detalle
            TipoDescuento deLaLista = idTipo == null ? null : tipoPorId(idTipo.toString());
            if (deLaLista != null) {
                this.tipoSeleccionado = deLaLista;
            }
            refrescarDetalle();
            limpiar();
            info(esNuevo ? "Descuento creado." : "Descuento modificado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar descuento", ex);
            error("No se pudo guardar el descuento: " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Fechas (la entidad puede usar Date, LocalDateTime, OffsetDateTime...)
    // ------------------------------------------------------------------
    /** Fecha y hora con formato dd/MM/yyyy HH:mm (vacío si es null). */
    public String fechaTexto(Object fecha) {
        LocalDateTime f = aLocalDateTime(fecha);
        return f == null ? "" : f.format(FORMATO_FECHA_HORA);
    }

    /** Igual que fechaTexto, pero null significa que el descuento no tiene fecha de fin. */
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

    // ------------------------------------------------------------------
    // Mensajes
    // ------------------------------------------------------------------
    private void info(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    // ------------------------------------------------------------------
    // Getters y setters
    // ------------------------------------------------------------------
    public List<TipoDescuento> getTiposDescuento() {
        return tiposDescuento;
    }

    public TipoDescuento getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public void setTipoSeleccionado(TipoDescuento tipoSeleccionado) {
        this.tipoSeleccionado = tipoSeleccionado;
    }

    public List<DescuentoProducto> getProductosDelTipo() {
        return productosDelTipo;
    }

    public String getIdTipoSel() {
        return idTipoSel;
    }

    public void setIdTipoSel(String idTipoSel) {
        this.idTipoSel = idTipoSel;
    }

    public boolean isSinFechaFin() {
        return sinFechaFin;
    }

    public void setSinFechaFin(boolean sinFechaFin) {
        this.sinFechaFin = sinFechaFin;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }

    public List<ColumnaTabla> getColumnasTipos() {
        return columnasTipos;
    }
}