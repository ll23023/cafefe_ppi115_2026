package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoTipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoProductoRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoTipoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoProducto;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class TipoProductoModel extends DefaultModel<TipoProducto, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(TipoProductoModel.class.getName());

    @Inject
    private TipoProductoRepository tipoProductoRepository;

    @Inject
    private ProductoTipoProductoRepository productoTipoProductoRepository;

    private boolean soloLectura = false;

    private List<Producto> productosDelTipo = new ArrayList<>();

    private List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    @Override
    public TipoProductoRepository getRepository() {
        return tipoProductoRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public TipoProducto instanciarRegistro() {
        TipoProducto t = new TipoProducto(UUID.randomUUID());
        t.setActivo(Boolean.TRUE);
        t.setObservaciones("");
        return t;
    }

    @Override
    public TipoProducto getRegistroById(Object id) {
        return id == null ? null : tipoProductoRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(TipoProducto dato) {
        return dato != null ? dato.getIdTipoProducto() : null;
    }

    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        refrescarProductos();
    }

    @Override
    public void limpiar() {
        super.limpiar();
        this.soloLectura = false;
        this.productosDelTipo = new ArrayList<>();
    }

    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(TipoProducto t) {
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

    /**
     * No permite eliminar un tipo de producto que tenga productos asociados.
     */
    public void eliminar(TipoProducto t) {
        if (t == null) {
            return;
        }

        try {
            int cantidad = productosConTipo(t).size();

            if (cantidad > 0) {
                error("No se puede eliminar \"" + t.getNombre() + "\": tiene "
                        + cantidad
                        + (cantidad == 1
                        ? " producto asociado."
                        : " productos asociados.")
                        + " Puede marcarlo como inactivo en su lugar.");
                return;
            }

            tipoProductoRepository.eliminar(t);
            cargarRegistros();
            limpiar();

            info("Tipo de producto eliminado.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar tipo de producto", ex);
            error("No se pudo eliminar el tipo de producto: " + ex.getMessage());
        }
    }

    @Override
    public void btnGuardarHandler() {
        if (soloLectura || registro == null) {
            return;
        }

        final String nombre = registro.getNombre() == null
                ? ""
                : registro.getNombre().trim();

        if (nombre.isEmpty()) {
            error("Indique el nombre del tipo de producto.");
            return;
        }

        registro.setNombre(nombre);

        boolean repetido = tipoProductoRepository.findAll().stream()
                .anyMatch(t -> t.getNombre() != null
                && t.getNombre().trim().equalsIgnoreCase(nombre)
                && !t.getIdTipoProducto().equals(registro.getIdTipoProducto()));

        if (repetido) {
            error("Ya existe un tipo de producto llamado \"" + nombre + "\".");
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
                tipoProductoRepository.crear(registro);
            } else {
                tipoProductoRepository.modificar(registro);
            }

            UUID id = registro.getIdTipoProducto();

            cargarRegistros();

            TipoProducto guardado = registros.stream()
                    .filter(t -> id.equals(t.getIdTipoProducto()))
                    .findFirst()
                    .orElse(null);

            if (guardado != null) {
                this.seleccion = guardado;
                mostrar(guardado);
            } else {
                limpiar();
            }

            info(esNuevo
                    ? "Tipo de producto creado."
                    : "Tipo de producto modificado.");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE,
                    "Error al guardar tipo de producto", ex);
            error("No se pudo guardar el tipo de producto: "
                    + ex.getMessage());
        }
    }

    /**
     * Carga el registro nuevamente desde la BD y prepara el detalle.
     */
    private void mostrar(TipoProducto t) {
        TipoProducto fresco = t.getIdTipoProducto() == null
                ? null
                : tipoProductoRepository.findById(t.getIdTipoProducto());

        this.registro = fresco != null ? fresco : t;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;

        refrescarProductos();
    }

    /**
     * Busca todos los productos que tienen asignado este tipo de producto.
     */
    private List<Producto> productosConTipo(TipoProducto tipo) {
        List<Producto> lista = new ArrayList<>();

        if (tipo == null
                || tipo.getIdTipoProducto() == null
                || productoTipoProductoRepository == null) {
            return lista;
        }

        UUID idTipo = tipo.getIdTipoProducto();

        productoTipoProductoRepository.findAll().stream()
                .filter(relacion -> relacion != null
                && relacion.getIdTipoProducto() != null
                && relacion.getIdProducto() != null
                && idTipo.equals(
                        relacion.getIdTipoProducto().getIdTipoProducto()))
                .map(ProductoTipoProducto::getIdProducto)
                .distinct()
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        a.getNombre() == null ? "" : a.getNombre(),
                        b.getNombre() == null ? "" : b.getNombre()))
                .forEach(lista::add);

        return lista;
    }

    /**
     * Refresca la lista de productos mostrados para el tipo seleccionado.
     */
    private void refrescarProductos() {
        if (seleccion != null) {
            this.productosDelTipo = productosConTipo(seleccion);
        } else {
            this.productosDelTipo = new ArrayList<>();
        }
    }

    public int cantidadProductos(TipoProducto tipo) {
        return tipo == null ? 0 : productosConTipo(tipo).size();
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
            return "Detalle del tipo de producto";
        }

        return isEditando()
                ? "Editar tipo de producto"
                : "Nuevo tipo de producto";
    }

    public List<Producto> getProductosDelTipo() {
        return productosDelTipo;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}