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

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.ProductoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.ProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class CaracteristicaModel extends DefaultModel<Caracteristica, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG =
            Logger.getLogger(CaracteristicaModel.class.getName());

    @Inject
    private CaracteristicaRepository caracteristicaRepository;

    @Inject
    private TipoCaracteristicaRepository tipoCaracteristicaRepository;

    @Inject
    private ProductoCaracteristicaRepository productoCaracteristicaRepository;

    private List<TipoCaracteristica> tipos = new ArrayList<>();

    private boolean soloLectura = false;

    private List<Producto> productosDeLaCaracteristica =
            new ArrayList<>();

    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("idTipoCaracteristica.nombre", "Tipo"),
            new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    @Override
    public CaracteristicaRepository getRepository() {
        return caracteristicaRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        cargarTipos();
    }

    @Override
    public Caracteristica instanciarRegistro() {
        Caracteristica c = new Caracteristica(UUID.randomUUID());
        c.setActivo(Boolean.TRUE);
        c.setObservaciones("");
        return c;
    }

    @Override
    public Caracteristica getRegistroById(Object id) {
        return id == null
                ? null
                : caracteristicaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(Caracteristica dato) {
        return dato != null
                ? dato.getIdCaracteristica()
                : null;
    }

    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        cargarTipos();
        refrescarProductos();
    }

    @Override
    public void limpiar() {
        super.limpiar();
        this.soloLectura = false;
        this.productosDeLaCaracteristica = new ArrayList<>();
        cargarTipos();
    }

    public List<TipoCaracteristica> getTipos() {
        if (this.tipos == null) {
            cargarTipos();
        }
        return tipos;
    }

    public void setTipos(List<TipoCaracteristica> tipos) {
        this.tipos = tipos;
    }

    private void cargarTipos() {
        this.tipos = tipoCaracteristicaRepository == null
                ? new ArrayList<>()
                : tipoCaracteristicaRepository.findAll().stream()
                        .sorted((a, b) ->
                                String.CASE_INSENSITIVE_ORDER.compare(
                                        a.getNombre() == null
                                                ? ""
                                                : a.getNombre(),
                                        b.getNombre() == null
                                                ? ""
                                                : b.getNombre()))
                        .toList();
    }

    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(Caracteristica c) {
        if (c == null) {
            return;
        }

        this.seleccion = c;
        mostrar(c);
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
     * No permite eliminar una característica
     * que tenga productos asociados.
     */
    public void eliminar(Caracteristica c) {
        if (c == null) {
            return;
        }

        try {
            int cantidad = productosDeLaCaracteristica(c).size();

            if (cantidad > 0) {
                error("No se puede eliminar \"" + c.getNombre()
                        + "\": está asociada a " + cantidad
                        + (cantidad == 1
                        ? " producto."
                        : " productos.")
                        + " Puede marcarla como inactiva en su lugar.");
                return;
            }

            caracteristicaRepository.eliminar(c);

            cargarRegistros();
            limpiar();

            info("Característica eliminada.");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE,
                    "Error al eliminar característica", ex);

            error("No se pudo eliminar la característica: "
                    + ex.getMessage());
        }
    }

    @Override
    public void btnGuardarHandler() {
        if (soloLectura || registro == null) {
            return;
        }

        String nombre = registro.getNombre() == null
                ? ""
                : registro.getNombre().trim();

        if (nombre.isEmpty()) {
            error("Indique el nombre de la característica.");
            return;
        }

        if (registro.getIdTipoCaracteristica() == null) {
            error("Seleccione el tipo de característica.");
            return;
        }

        if (isCreando() && !Boolean.TRUE.equals(
                registro.getIdTipoCaracteristica().getActivo())) {

            error("No se puede asignar un tipo de característica inactivo.");
            return;
        }

        registro.setNombre(nombre);

        boolean repetido = caracteristicaRepository.findAll()
                .stream()
                .anyMatch(c -> c.getNombre() != null
                && c.getNombre().trim().equalsIgnoreCase(nombre)
                && !c.getIdCaracteristica()
                        .equals(registro.getIdCaracteristica()));

        if (repetido) {
            error("Ya existe una característica llamada \""
                    + nombre + "\".");
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
                caracteristicaRepository.crear(registro);
            } else {
                caracteristicaRepository.modificar(registro);
            }

            UUID id = registro.getIdCaracteristica();

            cargarRegistros();

            Caracteristica guardada = registros.stream()
                    .filter(c -> id.equals(c.getIdCaracteristica()))
                    .findFirst()
                    .orElse(null);

            if (guardada != null) {
                this.seleccion = guardada;
                mostrar(guardada);
            } else {
                limpiar();
            }

            info(esNuevo
                    ? "Característica creada."
                    : "Característica modificada.");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE,
                    "Error al guardar característica", ex);

            error("No se pudo guardar la característica: "
                    + ex.getMessage());
        }
    }

    private void mostrar(Caracteristica c) {
        Caracteristica fresca =
                c.getIdCaracteristica() == null
                ? null
                : caracteristicaRepository
                        .findById(c.getIdCaracteristica());

        this.registro = fresca != null ? fresca : c;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;

        refrescarProductos();
    }

    private List<Producto> productosDeLaCaracteristica(
            Caracteristica c) {

        List<Producto> lista = new ArrayList<>();

        if (c == null
                || c.getIdCaracteristica() == null
                || productoCaracteristicaRepository == null) {
            return lista;
        }

        UUID idCaracteristica = c.getIdCaracteristica();

        productoCaracteristicaRepository.findAll()
                .stream()
                .filter(relacion -> relacion != null
                && relacion.getIdCaracteristica() != null
                && relacion.getIdProducto() != null
                && idCaracteristica.equals(
                        relacion.getIdCaracteristica()
                                .getIdCaracteristica()))
                .map(ProductoCaracteristica::getIdProducto)
                .distinct()
                .sorted((a, b) ->
                        String.CASE_INSENSITIVE_ORDER.compare(
                                a.getNombre() == null
                                        ? ""
                                        : a.getNombre(),
                                b.getNombre() == null
                                        ? ""
                                        : b.getNombre()))
                .forEach(lista::add);

        return lista;
    }

    private void refrescarProductos() {
        if (seleccion != null) {
            this.productosDeLaCaracteristica =
                    productosDeLaCaracteristica(seleccion);
        } else {
            this.productosDeLaCaracteristica =
                    new ArrayList<>();
        }
    }

    public int cantidadProductos(Caracteristica c) {
        return c == null
                ? 0
                : productosDeLaCaracteristica(c).size();
    }

    private void info(String msg) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        msg,
                        null));
    }

    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        msg,
                        null));
    }

    public boolean isSoloLectura() {
        return soloLectura;
    }

    public String getTituloFormulario() {
        if (soloLectura) {
            return "Detalle de la característica";
        }

        return isEditando()
                ? "Editar característica"
                : "Nueva característica";
    }

    public List<Producto> getProductosDeLaCaracteristica() {
        return productosDeLaCaracteristica;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}