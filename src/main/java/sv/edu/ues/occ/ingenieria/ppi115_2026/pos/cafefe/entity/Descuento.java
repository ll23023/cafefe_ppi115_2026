package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author mikelb
 */
@Entity
@Table(name = "descuento", catalog = "pos_ppi115_2026", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Descuento.findAll", query = "SELECT d FROM Descuento d"),
    @NamedQuery(name = "Descuento.findAllWithTipo", query = "SELECT d FROM Descuento d LEFT JOIN FETCH d.idTipoDescuento"),
    @NamedQuery(name = "Descuento.findByNombre", query = "SELECT d FROM Descuento d WHERE d.nombre = :nombre"),
    @NamedQuery(name = "Descuento.findByTipoDescuento", query = "SELECT d FROM Descuento d WHERE d.idTipoDescuento.idTipoDescuento = :idTipoDescuento"),
    @NamedQuery(name = "Descuento.findByFechaDesde", query = "SELECT d FROM Descuento d WHERE d.fechaDesde = :fechaDesde"),
    @NamedQuery(name = "Descuento.findByFechaHasta", query = "SELECT d FROM Descuento d WHERE d.fechaHasta = :fechaHasta"),
    @NamedQuery(name = "Descuento.findByObservaciones", query = "SELECT d FROM Descuento d WHERE d.observaciones = :observaciones")})
public class Descuento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_descuento", nullable = false)
    @Convert(converter = UUIDConverter.class)
    private UUID idDescuento;
    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;
    @Column(name = "fecha_desde")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDesde;
    @Column(name = "fecha_hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHasta;
    @Size(max = 2147483647)
    @Column(name = "observaciones", length = 2147483647)
    private String observaciones;
    @JoinColumn(name = "id_tipo_descuento", referencedColumnName = "id_tipo_descuento")
    @ManyToOne
    private TipoDescuento idTipoDescuento;
    @JsonbTransient
    @OneToMany(mappedBy = "idDescuento")
    private List<DescuentoProducto> descuentoProductoList;

    public Descuento() {
    }

    public Descuento(UUID idDescuento) {
        this.idDescuento = idDescuento;
    }

    public UUID getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(UUID idDescuento) {
        this.idDescuento = idDescuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public TipoDescuento getIdTipoDescuento() {
        return idTipoDescuento;
    }

    public void setIdTipoDescuento(TipoDescuento idTipoDescuento) {
        this.idTipoDescuento = idTipoDescuento;
    }

    public List<DescuentoProducto> getDescuentoProductoList() {
        return descuentoProductoList;
    }

    public void setDescuentoProductoList(List<DescuentoProducto> descuentoProductoList) {
        this.descuentoProductoList = descuentoProductoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDescuento != null ? idDescuento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Descuento)) {
            return false;
        }
        Descuento other = (Descuento) object;
        if ((this.idDescuento == null && other.idDescuento != null) || (this.idDescuento != null && !this.idDescuento.equals(other.idDescuento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Descuento[ idDescuento=" + idDescuento + " ]";
    }
    
}
