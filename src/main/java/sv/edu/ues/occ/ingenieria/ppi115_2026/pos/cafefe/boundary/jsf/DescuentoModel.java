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

    private List<TipoDescuento> tiposDescuento = new ArrayList<>();
    private List<DescuentoProducto> productosDelDescuento = new ArrayList<>();
    private String idTipoSel;

    @Override
    public DescuentoRepository getRepository() {
        return descuentoRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        cargarTipos();
        super.init();
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

    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        if (this.registros != null) {
            List<Descuento> ordenados = new ArrayList<>(this.registros);
            ordenados.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                    a.getNombre() == null ? "" : a.getNombre(),
                    b.getNombre() == null ? "" : b.getNombre()));
            this.registros = ordenados;
        }
        refrescarProductos();
    }

    @Override
    public void limpiar() {
        super.limpiar();             
        this.idTipoSel = null;
        refrescarProductos();
    }

    public void onSeleccionarDescuento() {
        refrescarProductos();
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

    private void refrescarProductos() {
        List<DescuentoProducto> lista = new ArrayList<>();
        if (seleccion != null && seleccion.getIdDescuento() != null && descuentoProductoRepository != null) {
            UUID idDescuento = seleccion.getIdDescuento();
            List<DescuentoProducto> todos = descuentoProductoRepository.findAll();
            if (todos != null) {
                for (DescuentoProducto dp : todos) {
                    if (dp != null
                            && dp.getIdDescuento() != null
                            && idDescuento.equals(dp.getIdDescuento().getIdDescuento())) {
                        lista.add(dp);
                    }
                }
            }
            lista.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                    a.getIdProducto() == null || a.getIdProducto().getNombre() == null ? "" : a.getIdProducto().getNombre(),
                    b.getIdProducto() == null || b.getIdProducto().getNombre() == null ? "" : b.getIdProducto().getNombre()));
        }
        this.productosDelDescuento = lista;
    }

    private Descuento buscarEnRegistros(UUID id) {
        if (id == null || registros == null) {
            return null;
        }
        return registros.stream()
                .filter(x -> id.equals(x.getIdDescuento()))
                .findFirst()
                .orElse(null);
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

    public void editar(Descuento d) {
        if (d == null) {
            return;
        }
        UUID id = d.getIdDescuento();
        cargarRegistros();
        Descuento fresco = buscarEnRegistros(id);
        if (fresco == null) {
            fresco = d;
        }
        seleccionarRegistro(fresco);
        this.seleccion = fresco;
        this.idTipoSel = (fresco.getIdTipoDescuento() != null
                && fresco.getIdTipoDescuento().getIdTipoDescuento() != null)
                ? fresco.getIdTipoDescuento().getIdTipoDescuento().toString()
                : null;
        refrescarProductos();
    }

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
            boolean eraElSeleccionado = seleccion != null && d.getIdDescuento() != null
                    && d.getIdDescuento().equals(seleccion.getIdDescuento());
            if (eraElSeleccionado) {
                this.seleccion = null;
            }
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

    @Override
    public void btnGuardarHandler() {
        if (registro == null) {
            return;
        }
        boolean esNuevo = isCreando();

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

        LocalDateTime hasta = aLocalDateTime(registro.getFechaHasta());
        if (hasta != null && hasta.isBefore(desde)) {
            error("La fecha de finalización no puede ser anterior a la fecha de inicio.");
            return;
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
            UUID idGuardado = registro.getIdDescuento();
            cargarRegistros();
            limpiar();
            this.seleccion = buscarEnRegistros(idGuardado);
            refrescarProductos();
            info(esNuevo ? "Descuento creado." : "Descuento modificado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar descuento", ex);
            error("No se pudo guardar el descuento: " + ex.getMessage());
        }
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

    public List<TipoDescuento> getTiposDescuento() {
        return tiposDescuento;
    }

    public List<DescuentoProducto> getProductosDelDescuento() {
        return productosDelDescuento;
    }

    public String getIdTipoSel() {
        return idTipoSel;
    }

    public void setIdTipoSel(String idTipoSel) {
        this.idTipoSel = idTipoSel;
    }
}