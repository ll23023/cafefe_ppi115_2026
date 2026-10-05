package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.DescuentoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoTipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Descuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.DescuentoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoTipoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class ProductoModel extends DefaultModel<Producto, UUID> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProductoModel.class.getName());

    @Inject
    private ProductoRepository productoRepository;
    @Inject
    private ProductoCaracteristicaRepository productoCaracteristicaRepository;
    @Inject
    private ProductoTipoProductoRepository productoTipoProductoRepository;
    @Inject
    private CaracteristicaRepository caracteristicaRepository;
    @Inject
    private TipoCaracteristicaRepository tipoCaracteristicaRepository;
    @Inject
    private TipoProductoRepository tipoProductoRepository;
    @Inject
    private DescuentoProductoRepository descuentoProductoRepository;
    @Inject
    private DescuentoRepository descuentoRepository;
    @Inject
    private sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository tipoDescuentoRepository;

    private List<Caracteristica> caracteristicas = new ArrayList<>();
    private List<TipoCaracteristica> tiposCaracteristica = new ArrayList<>();
    private List<TipoProducto> tiposProducto = new ArrayList<>();
    private List<Descuento> descuentos = new ArrayList<>();
    private List<sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento> tiposDescuento = new ArrayList<>();
    private final List<CaracteristicaFila> filasCaracteristicas = new ArrayList<>();
    private final List<TipoProductoFila> filasTiposProducto = new ArrayList<>();
    private final List<DescuentoFila> filasDescuentos = new ArrayList<>();
    private TipoCaracteristica nuevoTipoCaracteristica;
    private Descuento nuevoDescuento;
    private sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento nuevoTipoDescuento;
    private List<ColumnaTabla> columnas;
    private boolean soloLectura;

    /**
     * Una fila de la pestaña de características del producto.
     */
    public static class CaracteristicaFila implements Serializable {

        private static final long serialVersionUID = 1L;
        private TipoCaracteristica tipo;
        private Caracteristica caracteristica;
        private String tipoNombre;
        private String tipoId;
        private String caracteristicaNombre;
        private String valor;
        private boolean persistida;

        public TipoCaracteristica getTipo() {
            return tipo;
        }

        public void setTipo(TipoCaracteristica tipo) {
            this.tipo = tipo;
            this.tipoNombre = tipo == null ? null : tipo.getNombre();
            this.tipoId = tipo == null || tipo.getIdTipoCaracteristica() == null
                    ? null
                    : tipo.getIdTipoCaracteristica().toString();
        }

        public Caracteristica getCaracteristica() {
            return caracteristica;
        }

        public void setCaracteristica(Caracteristica caracteristica) {
            this.caracteristica = caracteristica;
            this.caracteristicaNombre = caracteristica == null ? null : caracteristica.getNombre();
        }

        public String getTipoNombre() {
            return tipoNombre;
        }

        public void setTipoNombre(String tipoNombre) {
            this.tipoNombre = tipoNombre;
        }

        public String getTipoId() {
            return tipoId;
        }

        public void setTipoId(String tipoId) {
            this.tipoId = tipoId;
        }

        public String getCaracteristicaNombre() {
            return caracteristicaNombre;
        }

        public void setCaracteristicaNombre(String caracteristicaNombre) {
            this.caracteristicaNombre = caracteristicaNombre;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public boolean isPersistida() {
            return persistida;
        }

        public void setPersistida(boolean persistida) {
            this.persistida = persistida;
        }
    }

    /**
     * Una fila de la pestaña de tipos de producto.
     */
    public static class TipoProductoFila implements Serializable {

        private static final long serialVersionUID = 1L;
        private TipoProducto tipo;
        private String tipoNombre;
        private boolean persistida;

        public TipoProducto getTipo() {
            return tipo;
        }

        public void setTipo(TipoProducto tipo) {
            this.tipo = tipo;
            this.tipoNombre = tipo == null ? null : tipo.getNombre();
        }

        public String getTipoNombre() {
            return tipoNombre;
        }

        public void setTipoNombre(String tipoNombre) {
            this.tipoNombre = tipoNombre;
        }

        public boolean isPersistida() {
            return persistida;
        }

        public void setPersistida(boolean persistida) {
            this.persistida = persistida;
        }
    }

    /**
     * Fila editable para asociar un descuento al producto.
     */
    public static class DescuentoFila implements Serializable {

        private static final long serialVersionUID = 1L;
        private sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento tipo;
        private String descuentoNombre;
        private Integer valor;
        private Date fechaDesde;
        private Date fechaHasta;
        private boolean persistida;

        public sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento getTipo() {
            return tipo;
        }

        public void setTipo(sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento tipo) {
            this.tipo = tipo;
        }

        public String getDescuentoNombre() {
            return descuentoNombre;
        }

        public void setDescuentoNombre(String descuentoNombre) {
            this.descuentoNombre = descuentoNombre;
        }

        public Integer getValor() {
            return valor;
        }

        public void setValor(Integer valor) {
            this.valor = valor;
        }

        public Date getFechaDesde() {
            return fechaDesde;
        }

        public void setFechaDesde(Date fechaDesde) {
            this.fechaDesde = fechaDesde;
        }

        public Date getFechaHasta() {
            return fechaHasta;
        }

        public void setFechaHasta(Date fechaHasta) {
            this.fechaHasta = fechaHasta;
        }

        public boolean isPersistida() {
            return persistida;
        }

        public void setPersistida(boolean persistida) {
            this.persistida = persistida;
        }
    }

    @Override
    public ProductoRepository getRepository() {
        return productoRepository;
    }

    @Override
    public Producto instanciarRegistro() {
        Producto p = new Producto(UUID.randomUUID());
        p.setActivo(Boolean.TRUE);
        p.setComentarios("");
        return p;
    }

    @Override
    public Producto getRegistroById(Object id) {
        return id == null ? null : productoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Producto dato) {
        return dato != null ? dato.getIdProducto() : null;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        cargarListasRelacionadas();
    }

    @Override
    public void limpiar() {
        super.limpiar();
        filasCaracteristicas.clear();
        filasTiposProducto.clear();
        filasDescuentos.clear();
        nuevoTipoCaracteristica = new TipoCaracteristica(UUID.randomUUID());
        nuevoTipoCaracteristica.setActivo(Boolean.TRUE);
        nuevoTipoCaracteristica.setObservaciones("");
        nuevoTipoDescuento = new sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento(UUID.randomUUID());
        nuevoTipoDescuento.setActivo(Boolean.TRUE);
        nuevoTipoDescuento.setDescuentoMaximo(90);
        nuevoTipoDescuento.setObservaciones("");
        nuevoDescuento = new Descuento(UUID.randomUUID());
        nuevoDescuento.setObservaciones("");
        this.soloLectura = false;
    }

    @Override
    public void seleccionarRegistro(Producto producto) {
        if (producto != null) {
            mostrar(producto);
        }
    }

    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(Producto producto) {
        if (producto == null) {
            return;
        }
        this.seleccion = producto;
        mostrar(producto);
        this.soloLectura = false;
        this.estado = ESTADO_CRUD.MODIFICAR;
    }

    public void cancelar() {
        limpiar();
    }

    private void mostrar(Producto producto) {
        if (producto == null || producto.getIdProducto() == null) {
            return;
        }
        Producto fresco = productoRepository.findById(producto.getIdProducto());
        this.registro = fresco != null ? fresco : producto;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;
        cargarRelaciones(this.registro.getIdProducto());
    }

    public void eliminar(Producto producto) {
        if (producto == null) {
            return;
        }
        productoCaracteristicaRepository.findAll().stream()
                .filter(relacion -> perteneceA(relacion.getIdProducto(), producto))
                .forEach(productoCaracteristicaRepository::eliminar);
        productoTipoProductoRepository.findAll().stream()
                .filter(relacion -> perteneceA(relacion.getIdProducto(), producto))
                .forEach(productoTipoProductoRepository::eliminar);
        descuentoProductoRepository.findAll().stream()
                .filter(relacion -> perteneceA(relacion.getIdProducto(), producto))
                .forEach(descuentoProductoRepository::eliminar);
        productoRepository.eliminar(producto);
        cargarRegistros();
        limpiar();
        mensaje(FacesMessage.SEVERITY_INFO, "Producto eliminado correctamente.");
    }

    private boolean perteneceA(Producto productoRelacionado, Producto producto) {
        return productoRelacionado != null && productoRelacionado.getIdProducto() != null
                && producto.getIdProducto() != null
                && producto.getIdProducto().equals(productoRelacionado.getIdProducto());
    }

    @Override
    @Transactional
    public void btnGuardarHandler() {
        if (soloLectura || registro == null || !validarRelaciones()) {
            return;
        }
        try {
            resolverRelacionesIngresadas();
            boolean nuevo = estado != ESTADO_CRUD.MODIFICAR;
            if (nuevo) {
                productoRepository.crear(registro);
            } else {
                productoRepository.modificar(registro);
            }
            Producto productoGuardado = productoRepository.findById(registro.getIdProducto());
            sincronizarCaracteristicas(productoGuardado);
            sincronizarTiposProducto(productoGuardado);
            sincronizarDescuentos(productoGuardado);
            UUID idGuardado = registro.getIdProducto();
            cargarRegistros();
            cargarListasRelacionadas();
            Producto guardado = registros.stream()
                    .filter(p -> idGuardado.equals(p.getIdProducto()))
                    .findFirst().orElse(null);
            if (guardado != null) {
                this.seleccion = guardado;
                mostrar(guardado);
            } else {
                limpiar();
            }
            mensaje(FacesMessage.SEVERITY_INFO, "Producto guardado correctamente.");
        } catch (RuntimeException exception) {
            LOG.log(Level.SEVERE, "Error al guardar producto y sus relaciones", exception);
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se pudo guardar el producto: " + exception.getMessage());
        }
    }

    private void cargarListasRelacionadas() {
        caracteristicas = new ArrayList<>(caracteristicaRepository.findAll().stream()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getNombre() == null ? "" : a.getNombre(),
                b.getNombre() == null ? "" : b.getNombre()))
                .toList());
        tiposCaracteristica = new ArrayList<>(tipoCaracteristicaRepository.findAll().stream()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getNombre() == null ? "" : a.getNombre(),
                b.getNombre() == null ? "" : b.getNombre()))
                .toList());
        tiposProducto = new ArrayList<>(tipoProductoRepository.findAll().stream()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getNombre() == null ? "" : a.getNombre(),
                b.getNombre() == null ? "" : b.getNombre()))
                .toList());
        descuentos = new ArrayList<>(descuentoRepository.findAllWithTipo().stream()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getNombre() == null ? "" : a.getNombre(),
                b.getNombre() == null ? "" : b.getNombre()))
                .toList());
        tiposDescuento = new ArrayList<>(tipoDescuentoRepository.findAll().stream()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getNombre() == null ? "" : a.getNombre(),
                b.getNombre() == null ? "" : b.getNombre()))
                .toList());
    }

    private void cargarRelaciones(UUID idProducto) {
        filasCaracteristicas.clear();
        productoCaracteristicaRepository.findByProducto(idProducto).forEach(relacion -> {
            CaracteristicaFila fila = new CaracteristicaFila();
            fila.setCaracteristica(relacion.getIdCaracteristica());
            fila.setTipo(relacion.getIdCaracteristica() == null
                    ? null : relacion.getIdCaracteristica().getIdTipoCaracteristica());
            fila.setTipoNombre(fila.getTipo() == null ? null : fila.getTipo().getNombre());
            fila.setCaracteristicaNombre(fila.getCaracteristica() == null
                    ? null : fila.getCaracteristica().getNombre());
            fila.setValor(relacion.getValor());
            fila.setPersistida(true);
            filasCaracteristicas.add(fila);
        });

        filasTiposProducto.clear();
        productoTipoProductoRepository.findByProducto(idProducto).forEach(relacion -> {
            TipoProductoFila fila = new TipoProductoFila();
            fila.setTipo(relacion.getIdTipoProducto());
            fila.setTipoNombre(fila.getTipo() == null ? null : fila.getTipo().getNombre());
            fila.setPersistida(true);
            filasTiposProducto.add(fila);
        });

        filasDescuentos.clear();
        descuentoProductoRepository.findByProducto(idProducto).forEach(relacion -> {
            DescuentoFila fila = new DescuentoFila();
            Descuento descuento = relacion.getIdDescuento();
            fila.setTipo(descuento == null ? null : descuento.getIdTipoDescuento());
            fila.setDescuentoNombre(descuento == null ? null : descuento.getNombre());
            fila.setValor(relacion.getValor());
            fila.setFechaDesde(relacion.getFechaDesde());
            fila.setFechaHasta(relacion.getFechaHasta());
            fila.setPersistida(true);
            filasDescuentos.add(fila);
        });
    }

    public List<Descuento> getDescuentosPorTipo(
            sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento tipo) {
        UUID idTipo = tipo == null ? null : tipo.getIdTipoDescuento();
        if (idTipo == null || descuentos == null) {
            return new ArrayList<>();
        }
        return descuentos.stream()
                .filter(d -> d != null
                && d.getIdTipoDescuento() != null
                && idTipo.equals(d.getIdTipoDescuento().getIdTipoDescuento()))
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getNombre() == null ? "" : a.getNombre(),
                b.getNombre() == null ? "" : b.getNombre()))
                .toList();
    }

    private boolean validarRelaciones() {
        if (registro == null || registro.getNombre() == null
                || registro.getNombre().isBlank()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El nombre del producto es obligatorio.");
            return false;
        }
        for (CaracteristicaFila fila : filasCaracteristicas) {
            if (fila.isPersistida()) {
                continue;
            }
            boolean vacia = fila.getTipo() == null
                    && (fila.getCaracteristicaNombre() == null || fila.getCaracteristicaNombre().isBlank())
                    && (fila.getValor() == null || fila.getValor().isBlank());
            if (vacia) {
                continue;
            }
            if (fila.getTipo() == null || fila.getCaracteristicaNombre() == null
                    || fila.getCaracteristicaNombre().isBlank()) {
                mensaje(FacesMessage.SEVERITY_ERROR, "Complete el tipo y la característica.");
                return false;
            }
            if (!Boolean.TRUE.equals(fila.getTipo().getActivo())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede asignar un tipo de característica inactivo: "
                        + fila.getTipo().getNombre());
                return false;
            }
        }
        for (DescuentoFila fila : filasDescuentos) {
            if (fila.isPersistida()) {
                continue;
            }

            boolean vacia = fila.getTipo() == null
                    && (fila.getDescuentoNombre() == null || fila.getDescuentoNombre().isBlank())
                    && fila.getValor() == null;
            if (vacia) {
                continue;
            }

            if (fila.getTipo() == null || fila.getDescuentoNombre() == null
                    || fila.getDescuentoNombre().isBlank() || fila.getValor() == null) {
                mensaje(FacesMessage.SEVERITY_ERROR, "Cada descuento debe tener tipo, nombre y valor.");
                return false;
            }

            if (!Boolean.TRUE.equals(fila.getTipo().getActivo())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede asignar un tipo de descuento inactivo: "
                        + fila.getTipo().getNombre());
                return false;
            }

            Descuento descuento = descuentos.stream()
                    .filter(d -> d.getNombre() != null
                    && d.getNombre().equalsIgnoreCase(fila.getDescuentoNombre().trim()))
                    .findFirst().orElse(null);
            if (descuento == null) {
                mensaje(FacesMessage.SEVERITY_ERROR, "El descuento seleccionado no existe.");
                return false;
            }

            if (descuento.getIdTipoDescuento() == null
                    || !fila.getTipo().getIdTipoDescuento().equals(
                            descuento.getIdTipoDescuento().getIdTipoDescuento())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "El descuento seleccionado no corresponde al tipo indicado.");
                return false;
            }

            if (!Boolean.TRUE.equals(descuento.getIdTipoDescuento().getActivo())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede asignar un descuento cuyo tipo está inactivo: "
                        + descuento.getNombre());
                return false;
            }

            Integer maximo = fila.getTipo().getDescuentoMaximo();

            if (maximo == null) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "El tipo de descuento seleccionado no tiene un máximo definido.");
                return false;
            }

            if (fila.getValor() > maximo) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "El valor del descuento \"" + descuento.getNombre()
                        + "\" no puede ser mayor a " + maximo + "%.");
                return false;
            }
        }

        for (CaracteristicaFila fila : filasCaracteristicas) {
            if (fila.getTipo() == null || fila.getCaracteristicaNombre() == null
                    || fila.getCaracteristicaNombre().isBlank()) {
                continue;
            }

            Caracteristica caracteristica = buscarCaracteristica(fila.getCaracteristicaNombre());
            if (caracteristica == null) {
                mensaje(FacesMessage.SEVERITY_ERROR, "La característica seleccionada no existe.");
                return false;
            }

            if (!Boolean.TRUE.equals(caracteristica.getActivo())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede asignar una característica inactiva: "
                        + caracteristica.getNombre());
                return false;
            }

            if (caracteristica.getIdTipoCaracteristica() != null
                    && !fila.getTipo().getIdTipoCaracteristica()
                            .equals(caracteristica.getIdTipoCaracteristica().getIdTipoCaracteristica())) {
                mensaje(FacesMessage.SEVERITY_ERROR, "El tipo no corresponde a la característica seleccionada.");
                return false;
            }

            String valor = fila.getValor() == null ? "" : fila.getValor().trim();
            String expresionRegular = fila.getTipo().getExpresionRegular();

            if (expresionRegular != null && !expresionRegular.isBlank()) {
                try {
                    if (!Pattern.matches(expresionRegular, valor)) {
                        mensaje(FacesMessage.SEVERITY_ERROR,
                                "El valor \"" + valor + "\" de la característica \""
                                + caracteristica.getNombre()
                                + "\" no cumple con la expresión regular del tipo \""
                                + fila.getTipo().getNombre() + "\".");
                        return false;
                    }
                } catch (PatternSyntaxException ex) {
                    mensaje(FacesMessage.SEVERITY_ERROR,
                            "La expresión regular del tipo de característica \""
                            + fila.getTipo().getNombre() + "\" no es válida.");
                    return false;
                }
            }
        }

        for (TipoProductoFila fila : filasTiposProducto) {
            if (fila.getTipo() == null) {
                continue;
            }
            if (!Boolean.TRUE.equals(fila.getTipo().getActivo())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede asignar un tipo de producto inactivo: "
                        + fila.getTipo().getNombre());
                return false;
            }
        }
        return true;
    }

    private void resolverRelacionesIngresadas() {
        for (CaracteristicaFila fila : filasCaracteristicas) {
            if (fila.getCaracteristicaNombre() == null || fila.getCaracteristicaNombre().isBlank()) {
                continue;
            }

            TipoCaracteristica tipo = fila.getTipo();
            Caracteristica caracteristica = buscarCaracteristica(fila.getCaracteristicaNombre());

            if (caracteristica == null) {
                caracteristica = new Caracteristica(UUID.randomUUID());
                caracteristica.setNombre(fila.getCaracteristicaNombre().trim());
                caracteristica.setActivo(Boolean.TRUE);
                caracteristica.setObservaciones("");
                caracteristica.setIdTipoCaracteristica(tipo);
                caracteristicaRepository.crear(caracteristica);
            }

            fila.setTipo(tipo);
            fila.setCaracteristica(caracteristica);
        }

        for (TipoProductoFila fila : filasTiposProducto) {
            if (fila.getTipoNombre() == null || fila.getTipoNombre().isBlank()) {
                continue;
            }

            TipoProducto tipo = tipoProductoRepository.findAll().stream()
                    .filter(t -> t.getNombre() != null && t.getNombre().equalsIgnoreCase(fila.getTipoNombre().trim()))
                    .findFirst().orElse(null);
            if (tipo == null) {
                tipo = new TipoProducto(UUID.randomUUID());
                tipo.setNombre(fila.getTipoNombre().trim());
                tipo.setActivo(Boolean.TRUE);
                tipo.setObservaciones("");
                tipoProductoRepository.crear(tipo);
            }

            fila.setTipo(tipo);
        }

        for (DescuentoFila fila : filasDescuentos) {
            if (fila.getTipo() == null || fila.getDescuentoNombre() == null
                    || fila.getDescuentoNombre().isBlank()) {
                continue;
            }

            Descuento descuento = descuentos.stream()
                    .filter(d -> d.getNombre() != null
                    && d.getNombre().equalsIgnoreCase(fila.getDescuentoNombre().trim())
                    && d.getIdTipoDescuento() != null
                    && fila.getTipo().getIdTipoDescuento() != null
                    && fila.getTipo().getIdTipoDescuento().equals(
                            d.getIdTipoDescuento().getIdTipoDescuento()))
                    .findFirst().orElse(null);
            if (descuento == null) {
                descuento = new Descuento(UUID.randomUUID());
                descuento.setNombre(fila.getDescuentoNombre().trim());
                descuento.setIdTipoDescuento(fila.getTipo());
                descuentoRepository.crear(descuento);
                descuentos.add(descuento);
            }

            if (fila.getFechaDesde() == null) {
                fila.setFechaDesde(descuento.getFechaDesde());
            }

            if (fila.getFechaHasta() == null) {
                fila.setFechaHasta(descuento.getFechaHasta());
            }

            fila.setDescuentoNombre(descuento.getNombre());
        }
    }

    private Caracteristica buscarCaracteristica(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        return caracteristicaRepository.findAll().stream()
                .filter(c -> c.getNombre() != null
                && c.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst().orElse(null);
    }

    public List<Caracteristica> getCaracteristicasPorTipo(TipoCaracteristica tipo) {
        if (tipo == null || tipo.getIdTipoCaracteristica() == null) {
            return new ArrayList<>();
        }

        UUID idTipo = tipo.getIdTipoCaracteristica();

        return caracteristicaRepository.findAll().stream()
                .filter(caracteristica -> caracteristica.getIdTipoCaracteristica() != null
                && idTipo.equals(
                        caracteristica.getIdTipoCaracteristica()
                                .getIdTipoCaracteristica()))
                .toList();
    }

    public List<Caracteristica> getCaracteristicasPorTipoId(String tipoId) {
        if (tipoId == null || tipoId.isBlank()) {
            return new ArrayList<>();
        }

        return caracteristicaRepository.findAll().stream()
                .filter(caracteristica -> caracteristica != null
                && caracteristica.getIdTipoCaracteristica() != null
                && caracteristica.getIdTipoCaracteristica()
                        .getIdTipoCaracteristica() != null
                && tipoId.equals(
                        caracteristica.getIdTipoCaracteristica()
                                .getIdTipoCaracteristica()
                                .toString()))
                .toList();
    }

    public void onCambioTipoCaracteristica(CaracteristicaFila fila) {
        if (fila == null || fila.isPersistida()) {
            return;
        }

        TipoCaracteristica tipo = null;

        if (fila.getTipoId() != null) {
            tipo = tiposCaracteristica.stream()
                    .filter(t -> t != null
                    && t.getIdTipoCaracteristica() != null
                    && fila.getTipoId().equals(
                            t.getIdTipoCaracteristica().toString()))
                    .findFirst()
                    .orElse(null);
        }

        fila.setTipo(tipo);
        fila.setCaracteristica(null);
        fila.setCaracteristicaNombre(null);
    }

    private TipoCaracteristica buscarTipoCaracteristicaPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }

        return tiposCaracteristica.stream()
                .filter(tipo -> tipo != null
                && tipo.getNombre() != null
                && tipo.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElse(null);
    }

    private void sincronizarCaracteristicas(Producto producto) {
        for (CaracteristicaFila fila : filasCaracteristicas) {
            if (fila.isPersistida() || fila.getCaracteristica() == null) {
                continue;
            }
            ProductoCaracteristica relacion = new ProductoCaracteristica(UUID.randomUUID());
            relacion.setIdProducto(producto);
            relacion.setIdCaracteristica(fila.getCaracteristica());
            relacion.setValor(fila.getValor());
            productoCaracteristicaRepository.crear(relacion);
            fila.setPersistida(true);
        }
    }

    private void sincronizarTiposProducto(Producto producto) {
        List<UUID> existentes = productoTipoProductoRepository.findByProducto(producto.getIdProducto()).stream()
                .filter(relacion -> relacion.getIdTipoProducto() != null)
                .map(relacion -> relacion.getIdTipoProducto().getIdTipoProducto())
                .toList();
        for (TipoProductoFila fila : filasTiposProducto) {
            if (fila.isPersistida() || fila.getTipo() == null
                    || existentes.contains(fila.getTipo().getIdTipoProducto())) {
                continue;
            }
            ProductoTipoProducto relacion = new ProductoTipoProducto(UUID.randomUUID());
            relacion.setIdProducto(producto);
            relacion.setIdTipoProducto(fila.getTipo());
            productoTipoProductoRepository.crear(relacion);
            fila.setPersistida(true);
        }
    }

    private void sincronizarDescuentos(Producto producto) {
        for (DescuentoFila fila : filasDescuentos) {
            if (fila.isPersistida()) {
                continue;
            }
            Descuento descuento = descuentos.stream()
                    .filter(d -> d.getNombre() != null && fila.getDescuentoNombre() != null
                    && d.getNombre().equalsIgnoreCase(fila.getDescuentoNombre().trim())
                    && d.getIdTipoDescuento() != null
                    && fila.getTipo() != null
                    && fila.getTipo().getIdTipoDescuento() != null
                    && fila.getTipo().getIdTipoDescuento().equals(
                            d.getIdTipoDescuento().getIdTipoDescuento()))
                    .findFirst().orElse(null);
            if (descuento == null) {
                throw new IllegalStateException(
                        "No se pudo resolver el descuento "
                        + fila.getDescuentoNombre() + "para el tipo seleccionado.");
            }
            DescuentoProducto relacion = new DescuentoProducto(UUID.randomUUID());
            relacion.setIdProducto(producto);
            relacion.setIdDescuento(descuento);
            relacion.setValor(fila.getValor());
            relacion.setFechaDesde(fila.getFechaDesde());
            relacion.setFechaHasta(fila.getFechaHasta());
            descuentoProductoRepository.crear(relacion);
            fila.setPersistida(true);
        }
    }

    public void agregarCaracteristica() {
        filasCaracteristicas.add(new CaracteristicaFila());
    }

    public void quitarCaracteristica(CaracteristicaFila fila) {
        if (fila != null && !fila.isPersistida()) {
            filasCaracteristicas.remove(fila);
        }
    }

    public void agregarTipoProducto() {
        filasTiposProducto.add(new TipoProductoFila());
    }

    public void quitarTipoProducto(TipoProductoFila fila) {
        if (fila != null && !fila.isPersistida()) {
            filasTiposProducto.remove(fila);
        }
    }

    public void agregarDescuento() {
        filasDescuentos.add(new DescuentoFila());
    }

    public void onCambioTipoDescuento(DescuentoFila fila) {
        if (fila != null && !fila.isPersistida()) {
            fila.setDescuentoNombre(null);
            fila.setFechaDesde(null);
            fila.setFechaHasta(null);
        }
    }

    public void quitarDescuento(DescuentoFila fila) {
        if (fila != null && !fila.isPersistida()) {
            filasDescuentos.remove(fila);
        }
    }

    public void prepararNuevoTipoCaracteristica() {
        nuevoTipoCaracteristica = new TipoCaracteristica(UUID.randomUUID());
        nuevoTipoCaracteristica.setActivo(Boolean.TRUE);
        nuevoTipoCaracteristica.setObservaciones("");
    }

    public void guardarTipoCaracteristica() {
        if (nuevoTipoCaracteristica == null || nuevoTipoCaracteristica.getNombre() == null
                || nuevoTipoCaracteristica.getNombre().isBlank()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El nombre del tipo de característica es obligatorio.");
            return;
        }
        tipoCaracteristicaRepository.crear(nuevoTipoCaracteristica);
        cargarListasRelacionadas();
        mensaje(FacesMessage.SEVERITY_INFO, "Tipo de característica creado.");
    }

    public void prepararNuevoTipoDescuento() {
        nuevoTipoDescuento = new sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento(UUID.randomUUID());
        nuevoTipoDescuento.setActivo(Boolean.TRUE);
        nuevoTipoDescuento.setDescuentoMaximo(90);
        nuevoTipoDescuento.setObservaciones("");
    }

    public void prepararNuevoDescuento() {
        nuevoDescuento = new Descuento(UUID.randomUUID());
        nuevoDescuento.setObservaciones("");
    }

    public void guardarDescuento() {
        if (nuevoDescuento == null || nuevoDescuento.getNombre() == null
                || nuevoDescuento.getNombre().isBlank()
                || nuevoDescuento.getIdTipoDescuento() == null
                || nuevoDescuento.getFechaDesde() == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Complete tipo, nombre y fecha inicial del descuento.");
            return;
        }
        if (nuevoDescuento.getFechaHasta() != null
                && nuevoDescuento.getFechaHasta().before(nuevoDescuento.getFechaDesde())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La fecha final no puede ser anterior a la fecha inicial.");
            return;
        }
        if (!Boolean.TRUE.equals(nuevoDescuento.getIdTipoDescuento().getActivo())) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se puede crear un descuento con un tipo de descuento inactivo: "
                    + nuevoDescuento.getIdTipoDescuento().getNombre());
            return;
        }
        descuentoRepository.crear(nuevoDescuento);
        descuentos.add(nuevoDescuento);
        DescuentoFila fila = new DescuentoFila();
        fila.setTipo(nuevoDescuento.getIdTipoDescuento());
        fila.setDescuentoNombre(nuevoDescuento.getNombre());
        fila.setFechaDesde(nuevoDescuento.getFechaDesde());
        fila.setFechaHasta(nuevoDescuento.getFechaHasta());
        filasDescuentos.add(fila);
        mensaje(FacesMessage.SEVERITY_INFO, "Descuento creado.");
    }

    public void guardarTipoDescuento() {
        if (nuevoTipoDescuento == null || nuevoTipoDescuento.getNombre() == null
                || nuevoTipoDescuento.getNombre().isBlank()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El nombre del tipo de descuento es obligatorio.");
            return;
        }
        tipoDescuentoRepository.crear(nuevoTipoDescuento);
        tiposDescuento.add(nuevoTipoDescuento);
        mensaje(FacesMessage.SEVERITY_INFO, "Tipo de descuento creado.");
    }

    private void mensaje(FacesMessage.Severity severity, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, texto, null));
    }

    public List<Caracteristica> getCaracteristicas() {
        return caracteristicas;
    }

    public List<TipoCaracteristica> getTiposCaracteristica() {
        return tiposCaracteristica;
    }

    public List<TipoProducto> getTiposProducto() {
        return tiposProducto;
    }

    public List<CaracteristicaFila> getFilasCaracteristicas() {
        return filasCaracteristicas;
    }

    public List<TipoProductoFila> getFilasTiposProducto() {
        return filasTiposProducto;
    }

    public TipoCaracteristica getNuevoTipoCaracteristica() {
        return nuevoTipoCaracteristica;
    }

    public void setNuevoTipoCaracteristica(TipoCaracteristica nuevoTipoCaracteristica) {
        this.nuevoTipoCaracteristica = nuevoTipoCaracteristica;
    }

    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    public List<sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento> getTiposDescuento() {
        return tiposDescuento;
    }

    public List<DescuentoFila> getFilasDescuentos() {
        return filasDescuentos;
    }

    public boolean isSoloLectura() {
        return soloLectura;
    }

    public sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoDescuento getNuevoTipoDescuento() {
        return nuevoTipoDescuento;
    }

    public Descuento getNuevoDescuento() {
        return nuevoDescuento;
    }

    public List<ColumnaTabla> getColumnas() {
        if (columnas == null) {
            columnas = List.of(
                    new ColumnaTabla("nombre", "Nombre"),
                    new ColumnaTabla("precioSugerido", "Precio sugerido", FORMATO_COLUMNA.CURRENCY),
                    new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
                    new ColumnaTabla("comentarios", "Comentarios"));
        }
        return columnas;
    }

    public String getTituloFormulario() {
        if (soloLectura) {
            return "Detalle del producto";
        }
        return isEditando() ? "Editar producto" : "Nuevo producto";
    }

}
