package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.DescuentoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Orden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.OrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.RolesOrden;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class OrdenModel extends DefaultModel<Orden, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(OrdenModel.class.getName());

    public static class ProductoFila implements Serializable {

        private static final long serialVersionUID = 1L;
        private UUID idOrdenProducto;
        private String nombreProducto;
        private BigDecimal precio;
        private String observaciones;
        private String idDescuentoSel;
        private final List<String> aplicados = new ArrayList<>();

        public ProductoFila() {
        }

        public ProductoFila(UUID idOrdenProducto, String nombreProducto, BigDecimal precio, String observaciones) {
            this.idOrdenProducto = idOrdenProducto;
            this.nombreProducto = nombreProducto;
            this.precio = precio;
            this.observaciones = observaciones;
        }

        public UUID getIdOrdenProducto() {
            return idOrdenProducto;
        }

        public void setIdOrdenProducto(UUID idOrdenProducto) {
            this.idOrdenProducto = idOrdenProducto;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public BigDecimal getPrecio() {
            return precio;
        }

        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }

        public String getIdDescuentoSel() {
            return idDescuentoSel;
        }

        public void setIdDescuentoSel(String idDescuentoSel) {
            this.idDescuentoSel = idDescuentoSel;
        }

        public List<String> getAplicados() {
            return aplicados;
        }

        public boolean isConDescuento() {
            return !aplicados.isEmpty();
        }

        public void limpiarDescuentos() {
            this.idDescuentoSel = null;
            this.aplicados.clear();
            this.observaciones = "";
        }
    }

    public static class OpcionDescuento implements Serializable {

        private static final long serialVersionUID = 1L;
        private final String id;
        private final String etiqueta;
        private final boolean aplicable;

        public OpcionDescuento(String id, String etiqueta, boolean aplicable) {
            this.id = id;
            this.etiqueta = etiqueta;
            this.aplicable = aplicable;
        }

        public String getId() {
            return id;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public boolean isAplicable() {
            return aplicable;
        }
    }

    private static final String PREFIJO_DESCUENTO = "-Aplicado ";
    private static final boolean COMENTARIO_CON_NOMBRE_DEL_TIPO = false;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject
    private OrdenRepository ordenRepository;

    @Inject
    private OrdenProductoRepository ordenProductoRepository;

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    @Inject
    private ProductoRepository productoRepository;

    @Inject
    private DescuentoProductoRepository descuentoProductoRepository;

    private boolean soloLectura = false;
    private Date fechaSeleccionada;
    private Date horaSeleccionada;
    private final List<ProductoFila> filas = new ArrayList<>();
    private List<EmpleadoRol> empleadoRoles;
    private List<Producto> productos = new ArrayList<>();
    private List<DescuentoProducto> descuentosProducto; // caché; se recarga al limpiar y al agregar fila
    private List<ColumnaTabla> columnas;

    @Override
    public OrdenRepository getRepository() {
        return ordenRepository;
    }

    @Override
    public Orden instanciarRegistro() {
        Orden r = new Orden(UUID.randomUUID());
        r.setFechaCreacion(new Date());
        return r;
    }

    @Override
    public Orden getRegistroById(Object id) {
        return id == null ? null : ordenRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Orden dato) {
        return dato != null ? dato.getIdOrden() : null;
    }

    @Override
    public void limpiar() {
        super.limpiar();
        Date ahora = new Date();
        this.fechaSeleccionada = ahora;
        this.horaSeleccionada = ahora;
        this.soloLectura = false;
        this.filas.clear();
        this.empleadoRoles = null;  
        this.descuentosProducto = null;
        this.cargarProductos();
    }

    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(Orden o) {
        this.seleccion = o;
        mostrar(o);
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

    public void eliminar(Orden o) {
        if (o == null) {
            return;
        }
        try {
            for (OrdenProducto op : asignacionesDe(o)) {
                ordenProductoRepository.eliminar(op);
            }
            ordenRepository.eliminar(o);
            cargarRegistros();
            limpiar();
            info("Orden eliminada.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar orden", ex);
            error("No se pudo eliminar la orden: " + ex.getMessage());
        }
    }

    public void agregarFila() {
        if (!isCreando()) {
            return;
        }
        cargarProductos();
        this.descuentosProducto = null;
        filas.add(new ProductoFila(null, "", null, ""));
    }

    public void quitarFila(ProductoFila fila) {
        if (!isCreando()) {
            return;
        }
        filas.remove(fila);
    }

    public void onCambioProducto(ProductoFila fila) {
        if (fila == null) {
            return;
        }
        fila.limpiarDescuentos();
        if (fila.getNombreProducto() == null || fila.getNombreProducto().isBlank()) {
            return;
        }
        Producto p = productoDeFila(fila);
        if (p != null) {
            fila.setPrecio(p.getPrecioSugerido());
        }
    }

    public boolean rolPermitidoParaOrden(EmpleadoRol er) {
        if (er == null || er.getIdRol() == null || er.getIdEmpleado() == null
                || er.getIdRol().getNombre() == null) {
            return false;
        }
        if (!Boolean.TRUE.equals(er.getActivo())
                || !Boolean.TRUE.equals(er.getIdRol().getActivo())
                || !Boolean.TRUE.equals(er.getIdEmpleado().getActivo())) {
            return false;
        }
        String nombre = er.getIdRol().getNombre().trim();
        return RolesOrden.getNombresRolesOrden().stream()
                .anyMatch(permitido -> permitido.equalsIgnoreCase(nombre));
    }

    @Override
    public void btnGuardarHandler() {
        if (soloLectura || registro == null) {
            return;
        }
        boolean esNuevo = isCreando();
        if (esNuevo) {
            if (!rolPermitidoParaOrden(registro.getIdEmpleadoRol())) {
                error("Solo puede tomar la orden un empleado activo con rol: "
                        + String.join(", ", RolesOrden.getNombresRolesOrden()));
                return;
            }
            if (!productosValidos()) {
                return;
            }
        } else {
            Orden original = ordenRepository.findById(registro.getIdOrden());
            if (original != null) {
                registro.setIdEmpleadoRol(original.getIdEmpleadoRol());
            }
        }
        combinarFechaHora();
        try {
            if (esNuevo) {
                crearConProductos();
            } else {
                ordenRepository.modificar(registro);
            }

            UUID id = registro.getIdOrden();
            cargarRegistros();
            Orden guardada = registros.stream()
                    .filter(o -> id.equals(o.getIdOrden()))
                    .findFirst().orElse(null);
            if (guardada != null) {
                this.seleccion = guardada;
                mostrar(guardada);
            } else {
                limpiar();
            }
            info("Orden guardada.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar orden", ex);
            error("No se pudo guardar la orden: " + ex.getMessage());
        }
    }

    private void crearConProductos() {
        ordenRepository.crear(registro);
        Orden ord = ordenRepository.findById(registro.getIdOrden());
        try {
            guardarProductos(ord);
        } catch (RuntimeException ex) {
            for (OrdenProducto op : asignacionesDe(ord)) {
                ordenProductoRepository.eliminar(op);
            }
            ordenRepository.eliminar(ord);
            throw ex;
        }
    }

    private void combinarFechaHora() {
        if (this.registro != null) {
            Calendar calFecha = Calendar.getInstance();
            Calendar calHora = Calendar.getInstance();

            if (this.fechaSeleccionada != null) {
                calFecha.setTime(this.fechaSeleccionada);
            }
            if (this.horaSeleccionada != null) {
                calHora.setTime(this.horaSeleccionada);
            }

            Calendar combinado = Calendar.getInstance();
            combinado.set(Calendar.YEAR, calFecha.get(Calendar.YEAR));
            combinado.set(Calendar.MONTH, calFecha.get(Calendar.MONTH));
            combinado.set(Calendar.DAY_OF_MONTH, calFecha.get(Calendar.DAY_OF_MONTH));
            combinado.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
            combinado.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
            combinado.set(Calendar.SECOND, 0);
            combinado.set(Calendar.MILLISECOND, 0);
            this.registro.setFechaCreacion(combinado.getTime());
        }
    }

    private Map<String, Producto> productosPorNombre() {
        Map<String, Producto> porNombre = new HashMap<>();
        for (Producto p : productos) {
            if (p.getNombre() != null) {
                porNombre.putIfAbsent(clave(p.getNombre()), p);
            }
        }
        return porNombre;
    }

    private boolean productosValidos() {
        Map<String, Producto> porNombre = productosPorNombre();
        for (ProductoFila f : filas) {
            String nombre = f.getNombreProducto() == null ? "" : f.getNombreProducto().trim();
            if (nombre.isEmpty()) {
                continue;
            }
            Producto existente = porNombre.get(clave(nombre));
            if (existente == null) {
                error("El producto no existe: " + nombre);
                return false;
            }
            if (!Boolean.TRUE.equals(existente.getActivo())) {
                error("No se puede agregar un producto inactivo: " + existente.getNombre());
                return false;
            }
            if (f.getPrecio() == null) {
                error("Indique el precio para: " + nombre);
                return false;
            }
            LocalDate fechaOrden = fechaDeLaOrden();
            for (String idDp : f.getAplicados()) {
                DescuentoProducto dp = descuentoPorId(idDp);
                if (dp == null || !aplicaEnFecha(dp, fechaOrden)) {
                    error("Un descuento aplicado a \"" + nombre + "\" no aplica para la fecha de la orden ("
                            + fechaOrden.format(FORMATO_FECHA)
                            + "). Quite ese producto de la orden y vuelva a agregarlo, o cambie la fecha.");
                    return false;
                }
            }
        }
        return true;
    }

    public List<OpcionDescuento> opcionesDescuento(ProductoFila fila) {
        List<OpcionDescuento> opciones = new ArrayList<>();
        Producto producto = productoDeFila(fila);
        if (producto == null) {
            return opciones;
        }
        LocalDate fechaOrden = fechaDeLaOrden();
        for (DescuentoProducto dp : descuentosCargados()) {
            if (dp.getIdDescuentoProducto() == null || !esDelProducto(dp, producto)) {
                continue;
            }
            boolean aplica = aplicaEnFecha(dp, fechaOrden);
            Integer valor = aEntero(dp.getValor());
            String tipo = nombreTipo(dp);
            String etiqueta = nombreDescuento(dp)
                    + (tipo != null ? " [" + tipo + "]" : "")
                    + " - " + (valor != null ? valor + "%" : "?")
                    + (dp.getIdProducto() == null ? " (todos los productos)" : "")
                    + (aplica ? "" : " - no aplica en la fecha de la orden");
            opciones.add(new OpcionDescuento(dp.getIdDescuentoProducto().toString(), etiqueta, aplica));
        }
        opciones.sort((a, b) -> {
            if (a.isAplicable() != b.isAplicable()) {
                return a.isAplicable() ? -1 : 1;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(a.getEtiqueta(), b.getEtiqueta());
        });
        return opciones;
    }

    public void aplicarDescuento(ProductoFila fila) {
        if (!isCreando() || fila == null) {
            return;
        }
        Producto producto = productoDeFila(fila);
        if (producto == null) {
            error("Seleccione primero un producto.");
            return;
        }
        DescuentoProducto dp = descuentoPorId(fila.getIdDescuentoSel());
        if (dp == null) {
            error("Seleccione un descuento para aplicar.");
            return;
        }
        if (!esDelProducto(dp, producto)) {
            error("Ese descuento no corresponde al producto seleccionado.");
            return;
        }
        String nombre = nombreDescuento(dp);
        LocalDate fechaOrden = fechaDeLaOrden();
        if (!aplicaEnFecha(dp, fechaOrden)) {
            error("El descuento \"" + nombre + "\" no aplica para la fecha de la orden ("
                    + fechaOrden.format(FORMATO_FECHA) + ").");
            return;
        }
        Integer valor = aEntero(dp.getValor());
        if (valor == null || valor <= 0 || valor > 100) {
            error("El descuento \"" + nombre + "\" tiene un valor inválido.");
            return;
        }
        if (fila.getPrecio() == null) {
            error("El producto no tiene precio.");
            return;
        }

        BigDecimal factor = BigDecimal.valueOf(100L - valor).divide(BigDecimal.valueOf(100));
        fila.setPrecio(fila.getPrecio().multiply(factor).setScale(2, RoundingMode.HALF_UP));

        String nota = PREFIJO_DESCUENTO + nombreParaComentario(dp) + " (" + valor + "%)";
        String actual = fila.getObservaciones() == null ? "" : fila.getObservaciones().trim();
        fila.setObservaciones(actual.isEmpty() ? nota : actual + " " + nota);
        fila.getAplicados().add(dp.getIdDescuentoProducto().toString());
        info("Descuento aplicado: " + nota.substring(PREFIJO_DESCUENTO.length()) + ".");
    }

    private List<DescuentoProducto> descuentosCargados() {
        if (this.descuentosProducto == null) {
            if (descuentoProductoRepository != null) {
                this.descuentosProducto = descuentoProductoRepository.findAll();
            } else {
                this.descuentosProducto = new ArrayList<>();
            }
        }
        return this.descuentosProducto;
    }

    private DescuentoProducto descuentoPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (DescuentoProducto dp : descuentosCargados()) {
            if (dp.getIdDescuentoProducto() != null && id.equals(dp.getIdDescuentoProducto().toString())) {
                return dp;
            }
        }
        return null;
    }

    private Producto productoDeFila(ProductoFila fila) {
        if (fila == null || fila.getNombreProducto() == null || fila.getNombreProducto().isBlank()) {
            return null;
        }
        return productosPorNombre().get(clave(fila.getNombreProducto()));
    }

    private boolean esDelProducto(DescuentoProducto dp, Producto p) {
        if (dp.getIdProducto() == null) {
            return true;
        }
        return p.getIdProducto() != null && p.getIdProducto().equals(dp.getIdProducto().getIdProducto());
    }

    private static String nombreDescuento(DescuentoProducto dp) {
        var d = dp.getIdDescuento();
        String n = d != null ? d.getNombre() : null;
        return n == null || n.isBlank() ? "Descuento" : n.trim();
    }

    private static String nombreTipo(DescuentoProducto dp) {
        var d = dp.getIdDescuento();
        var t = d != null ? d.getIdTipoDescuento() : null;
        String n = t != null ? t.getNombre() : null;
        return n == null || n.isBlank() ? null : n.trim();
    }

    private static String nombreParaComentario(DescuentoProducto dp) {
        if (COMENTARIO_CON_NOMBRE_DEL_TIPO) {
            String tipo = nombreTipo(dp);
            if (tipo != null) {
                return tipo;
            }
        }
        return nombreDescuento(dp);
    }

    private LocalDate fechaDeLaOrden() {
        LocalDate f = aLocalDate(fechaSeleccionada);
        return f != null ? f : LocalDate.now();
    }

    private boolean aplicaEnFecha(DescuentoProducto dp, LocalDate fecha) {
        if (fecha == null) {
            return false;
        }
        var d = dp.getIdDescuento();
        boolean enDescuento = d == null
                || dentro(fecha, aLocalDate(d.getFechaDesde()), aLocalDate(d.getFechaHasta()));
        boolean enProducto = dentro(fecha, aLocalDate(dp.getFechaDesde()), aLocalDate(dp.getFechaHasta()));
        return enDescuento && enProducto;
    }

    private static boolean dentro(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
    }

    private static LocalDate aLocalDate(Object o) {
        if (o == null) {
            return null;
        }
        ZoneId zona = ZoneId.systemDefault();
        if (o instanceof LocalDate ld) {
            return ld;
        }
        if (o instanceof LocalDateTime ldt) {
            return ldt.toLocalDate();
        }
        if (o instanceof OffsetDateTime odt) {
            return odt.atZoneSameInstant(zona).toLocalDate();
        }
        if (o instanceof ZonedDateTime zdt) {
            return zdt.withZoneSameInstant(zona).toLocalDate();
        }
        if (o instanceof Instant i) {
            return i.atZone(zona).toLocalDate();
        }
        if (o instanceof java.sql.Date sd) {
            return sd.toLocalDate();
        }
        if (o instanceof Date d) {
            return d.toInstant().atZone(zona).toLocalDate();
        }
        return null;
    }

    private static Integer aEntero(Object o) {
        return o instanceof Number n ? n.intValue() : null;
    }

    private void guardarProductos(Orden orden) {
        Map<String, Producto> porNombre = productosPorNombre();
        for (ProductoFila f : filas) {
            String nombre = f.getNombreProducto() == null ? "" : f.getNombreProducto().trim();
            if (nombre.isEmpty()) {
                continue;
            }
            Producto producto = porNombre.get(clave(nombre));
            if (producto == null) {
                continue;
            }
            OrdenProducto op = new OrdenProducto(UUID.randomUUID());
            op.setIdOrden(orden);
            op.setIdProducto(producto);
            op.setPrecio(f.getPrecio());
            op.setObservaciones(f.getObservaciones() != null ? f.getObservaciones() : "");
            ordenProductoRepository.crear(op);
        }
        cargarProductos();
    }

    private void mostrar(Orden o) {
        Orden fresca = ordenRepository.findById(o.getIdOrden());
        this.registro = fresca != null ? fresca : o;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;
        if (this.registro.getFechaCreacion() != null) {
            this.fechaSeleccionada = this.registro.getFechaCreacion();
            this.horaSeleccionada = this.registro.getFechaCreacion();
        }
        this.filas.clear();
        for (OrdenProducto op : asignacionesDe(this.registro)) {
            String nombre = op.getIdProducto() != null ? op.getIdProducto().getNombre() : "";
            filas.add(new ProductoFila(op.getIdOrdenProducto(), nombre, op.getPrecio(), op.getObservaciones()));
        }
    }

    private List<OrdenProducto> asignacionesDe(Orden o) {
        if (o == null || o.getIdOrden() == null) {
            return new ArrayList<>();
        }
        return ordenProductoRepository.findAll().stream()
                .filter(op -> op.getIdOrden() != null
                && o.getIdOrden().equals(op.getIdOrden().getIdOrden()))
                .toList();
    }

    private void cargarProductos() {
        this.productos = productoRepository.findAll().stream()
                .sorted((p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(
                        p1.getNombre() == null ? "" : p1.getNombre(),
                        p2.getNombre() == null ? "" : p2.getNombre()))
                .toList();
    }

    private static String clave(String nombre) {
        return nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
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
            return "Detalle de la orden";
        }
        return isEditando() ? "Editar orden" : "Nueva orden";
    }

    public List<ProductoFila> getFilas() {
        return filas;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<EmpleadoRol> getEmpleadoRoles() {
        if (this.empleadoRoles == null) {
            this.empleadoRoles = empleadoRolRepository == null ? new ArrayList<>()
                    : empleadoRolRepository.findAll().stream()
                            .filter(this::rolPermitidoParaOrden)
                            .toList();
        }
        EmpleadoRol actual = registro != null ? registro.getIdEmpleadoRol() : null;
        if (!isCreando() && actual != null
                && empleadoRoles.stream().noneMatch(x -> x.getIdEmpleadoRol().equals(actual.getIdEmpleadoRol()))) {
            List<EmpleadoRol> conActual = new ArrayList<>(empleadoRoles);
            conActual.add(actual);
            return conActual;
        }
        return empleadoRoles;
    }

    public void setEmpleadoRoles(List<EmpleadoRol> empleadoRoles) {
        this.empleadoRoles = empleadoRoles;
    }

    public Date getFechaSeleccionada() {
        return fechaSeleccionada;
    }

    public void setFechaSeleccionada(Date fechaSeleccionada) {
        this.fechaSeleccionada = fechaSeleccionada;
    }

    public Date getHoraSeleccionada() {
        return horaSeleccionada;
    }

    public void setHoraSeleccionada(Date horaSeleccionada) {
        this.horaSeleccionada = horaSeleccionada;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("idEmpleadoRol.idEmpleado.nombre", "Empleado"),
                    new ColumnaTabla("idEmpleadoRol.idEmpleado.apellido", "Apellido"),
                    new ColumnaTabla("idEmpleadoRol.idRol.nombre", "Rol"),
                    new ColumnaTabla("fechaCreacion", "Fecha", FORMATO_COLUMNA.DATE)
            );
        }
        return columnas;
    }
}