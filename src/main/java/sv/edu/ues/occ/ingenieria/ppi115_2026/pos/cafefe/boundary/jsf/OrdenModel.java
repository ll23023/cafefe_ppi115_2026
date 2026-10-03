package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
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
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.EmpleadoRolRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.OrdenRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
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
    }

    @Inject
    private OrdenRepository ordenRepository;

    @Inject
    private OrdenProductoRepository ordenProductoRepository;

    @Inject
    private EmpleadoRolRepository empleadoRolRepository;

    @Inject
    private ProductoRepository productoRepository;

    private boolean soloLectura = false;
    private Date fechaSeleccionada;
    private Date horaSeleccionada;
    private final List<ProductoFila> filas = new ArrayList<>();
    private List<EmpleadoRol> empleadoRoles;
    private List<Producto> productos = new ArrayList<>();
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
        this.empleadoRoles = null; // se vuelve a cargar al pedirlos
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

    // Los productos solo se pueden agregar/quitar al crear (también se protege en servidor).
    public void agregarFila() {
        if (!isCreando()) {
            return;
        }
        cargarProductos();
        filas.add(new ProductoFila(null, "", null, ""));
    }

    public void quitarFila(ProductoFila fila) {
        if (!isCreando()) {
            return;
        }
        filas.remove(fila);
    }

    public void onCambioProducto(ProductoFila fila) {
        if (fila == null || fila.getNombreProducto() == null || fila.getNombreProducto().isBlank()) {
            return;
        }
        String k = clave(fila.getNombreProducto());
        for (Producto p : productos) {
            if (k.equals(clave(p.getNombre()))) {
                fila.setPrecio(p.getPrecioSugerido());
                return;
            }
        }
    }

    /**
     * Un empleado puede tomar la orden solo si la asignación, el rol y el
     * empleado están activos y el rol es uno de {@link RolesOrden}
     * (debe contener únicamente "camarero").
     */
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
            // El empleado no cambia al modificar: se restaura desde la BD
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

    /**
     * Crea la orden y sus productos. Si falla algo se deshace lo creado para no
     * dejar una orden a medias.
     */
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

    // Cada producto debe existir (se crea en la pantalla de Productos) y estar activo.
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
        }
        return true;
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

    /**
     * Al crear: solo asignaciones activas con rol permitido (camarero). Al ver o
     * modificar se incluye además la asignación actual de la orden, para que el
     * selector (deshabilitado) siga mostrando quién la tomó.
     */
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