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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.CaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoCaracteristicaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author mil
 */
@Named
@ViewScoped
public class TipoCaracteristicaModel extends DefaultModel<TipoCaracteristica, UUID> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG =
            Logger.getLogger(TipoCaracteristicaModel.class.getName());

    @Inject
    private TipoCaracteristicaRepository tipoCaracteristicaRepository;

    @Inject
    private CaracteristicaRepository caracteristicaRepository;

    private boolean soloLectura = false;

    private List<Caracteristica> caracteristicasDelTipo =
            new ArrayList<>();

    private final List<ColumnaTabla> columnas = Arrays.asList(
            new ColumnaTabla("nombre", "Nombre"),
            new ColumnaTabla("expresionRegular", "Expresión regular"),
            new ColumnaTabla("activo", "Estado", FORMATO_COLUMNA.BOOLEAN),
            new ColumnaTabla("observaciones", "Observaciones")
    );

    @Override
    public TipoCaracteristicaRepository getRepository() {
        return tipoCaracteristicaRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public TipoCaracteristica instanciarRegistro() {
        TipoCaracteristica t = new TipoCaracteristica(UUID.randomUUID());
        t.setActivo(Boolean.TRUE);
        t.setObservaciones("");
        return t;
    }

    @Override
    public TipoCaracteristica getRegistroById(Object id) {
        return id == null
                ? null
                : tipoCaracteristicaRepository.findById((UUID) id);
    }

    @Override
    public Object getIdByRegistro(TipoCaracteristica dato) {
        return dato != null
                ? dato.getIdTipoCaracteristica()
                : null;
    }

    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        refrescarCaracteristicas();
    }

    @Override
    public void limpiar() {
        super.limpiar();
        this.soloLectura = false;
        this.caracteristicasDelTipo = new ArrayList<>();
    }

    public void onSeleccionar() {
        if (seleccion != null) {
            mostrar(seleccion);
        }
    }

    public void editar(TipoCaracteristica t) {
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
     * No permite eliminar un tipo de característica
     * que tenga características asociadas.
     */
    public void eliminar(TipoCaracteristica t) {
        if (t == null) {
            return;
        }

        try {
            int cantidad = caracteristicasConTipo(t).size();

            if (cantidad > 0) {
                error("No se puede eliminar \"" + t.getNombre()
                        + "\": tiene " + cantidad
                        + (cantidad == 1
                        ? " característica asociada."
                        : " características asociadas.")
                        + " Puede marcarlo como inactivo en su lugar.");
                return;
            }

            tipoCaracteristicaRepository.eliminar(t);
            cargarRegistros();
            limpiar();

            info("Tipo de característica eliminado.");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE,
                    "Error al eliminar tipo de característica", ex);

            error("No se pudo eliminar el tipo de característica: "
                    + ex.getMessage());
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
            error("Indique el nombre del tipo de característica.");
            return;
        }

        registro.setNombre(nombre);

        String expresionRegular = registro.getExpresionRegular();

        if (expresionRegular != null && !expresionRegular.isBlank()) {
            try {
                Pattern.compile(expresionRegular);
            } catch (PatternSyntaxException ex) {
                error("La expresión regular indicada no es válida.");
                return;
            }
        }

        registro.setNombre(nombre);

        boolean repetido = tipoCaracteristicaRepository.findAll()
                .stream()
                .anyMatch(t -> t.getNombre() != null
                && t.getNombre().trim().equalsIgnoreCase(nombre)
                && !t.getIdTipoCaracteristica()
                        .equals(registro.getIdTipoCaracteristica()));

        if (repetido) {
            error("Ya existe un tipo de característica llamado \""
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
                tipoCaracteristicaRepository.crear(registro);
            } else {
                tipoCaracteristicaRepository.modificar(registro);
            }

            UUID id = registro.getIdTipoCaracteristica();

            cargarRegistros();

            TipoCaracteristica guardado = registros.stream()
                    .filter(t -> id.equals(t.getIdTipoCaracteristica()))
                    .findFirst()
                    .orElse(null);

            if (guardado != null) {
                this.seleccion = guardado;
                mostrar(guardado);
            } else {
                limpiar();
            }

            info(esNuevo
                    ? "Tipo de característica creado."
                    : "Tipo de característica modificado.");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE,
                    "Error al guardar tipo de característica", ex);

            error("No se pudo guardar el tipo de característica: "
                    + ex.getMessage());
        }
    }

    /**
     * Carga nuevamente el registro desde la BD
     * y prepara el detalle.
     */
    private void mostrar(TipoCaracteristica t) {
        TipoCaracteristica fresco = t.getIdTipoCaracteristica() == null
                ? null
                : tipoCaracteristicaRepository
                        .findById(t.getIdTipoCaracteristica());

        this.registro = fresco != null ? fresco : t;
        this.estado = ESTADO_CRUD.NINGUNO;
        this.soloLectura = true;

        refrescarCaracteristicas();
    }

    /**
     * Busca todas las características que pertenecen
     * al tipo seleccionado.
     */
    private List<Caracteristica> caracteristicasConTipo(
            TipoCaracteristica tipo) {

        List<Caracteristica> lista = new ArrayList<>();

        if (tipo == null
                || tipo.getIdTipoCaracteristica() == null
                || caracteristicaRepository == null) {
            return lista;
        }

        UUID idTipo = tipo.getIdTipoCaracteristica();

        caracteristicaRepository.findAll()
                .stream()
                .filter(c -> c != null
                && c.getIdTipoCaracteristica() != null
                && idTipo.equals(
                        c.getIdTipoCaracteristica()
                                .getIdTipoCaracteristica()))
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

    /**
     * Refresca el detalle de características
     * correspondiente al tipo seleccionado.
     */
    private void refrescarCaracteristicas() {
        if (seleccion != null) {
            this.caracteristicasDelTipo =
                    caracteristicasConTipo(seleccion);
        } else {
            this.caracteristicasDelTipo =
                    new ArrayList<>();
        }
    }

    public int cantidadCaracteristicas(TipoCaracteristica tipo) {
        return tipo == null
                ? 0
                : caracteristicasConTipo(tipo).size();
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
            return "Detalle del tipo de característica";
        }

        return isEditando()
                ? "Editar tipo de característica"
                : "Nuevo tipo de característica";
    }

    public List<Caracteristica> getCaracteristicasDelTipo() {
        return caracteristicasDelTipo;
    }

    public List<ColumnaTabla> getColumnas() {
        return columnas;
    }
}