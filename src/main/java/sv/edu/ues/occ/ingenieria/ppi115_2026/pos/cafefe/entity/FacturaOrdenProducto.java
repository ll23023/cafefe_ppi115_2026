package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author mikelb
 */
@Entity
@Table(name = "factura_orden_producto", catalog = "pos_ppi115_2026", schema = "public")
@NamedQueries({
    @NamedQuery(name = "FacturaOrdenProducto.findAll", query = "SELECT f FROM FacturaOrdenProducto f"),
    @NamedQuery(name = "FacturaOrdenProducto.findByPrecio", query = "SELECT f FROM FacturaOrdenProducto f WHERE f.precio = :precio"),
    @NamedQuery(name = "FacturaOrdenProducto.findByObservaciones", query = "SELECT f FROM FacturaOrdenProducto f WHERE f.observaciones = :observaciones")})
public class FacturaOrdenProducto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_factura_orden_producto", nullable = false)
    @Convert(converter = UUIDConverter.class)
    private UUID idFacturaOrdenProducto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precio", precision = 8, scale = 2)
    private BigDecimal precio;
    @Size(max = 2147483647)
    @Column(name = "observaciones", length = 2147483647)
    private String observaciones;
    @JoinColumn(name = "id_factura", referencedColumnName = "id_factura")
    @ManyToOne
    private Factura idFactura;
    @JoinColumn(name = "id_orden_producto", referencedColumnName = "id_orden_producto")
    @ManyToOne
    private OrdenProducto idOrdenProducto;

    public FacturaOrdenProducto() {
    }

    public FacturaOrdenProducto(UUID idFacturaOrdenProducto) {
        this.idFacturaOrdenProducto = idFacturaOrdenProducto;
    }

    public UUID getIdFacturaOrdenProducto() {
        return idFacturaOrdenProducto;
    }

    public void setIdFacturaOrdenProducto(UUID idFacturaOrdenProducto) {
        this.idFacturaOrdenProducto = idFacturaOrdenProducto;
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

    public Factura getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Factura idFactura) {
        this.idFactura = idFactura;
    }

    public OrdenProducto getIdOrdenProducto() {
        return idOrdenProducto;
    }

    public void setIdOrdenProducto(OrdenProducto idOrdenProducto) {
        this.idOrdenProducto = idOrdenProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFacturaOrdenProducto != null ? idFacturaOrdenProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FacturaOrdenProducto)) {
            return false;
        }
        FacturaOrdenProducto other = (FacturaOrdenProducto) object;
        if ((this.idFacturaOrdenProducto == null && other.idFacturaOrdenProducto != null) || (this.idFacturaOrdenProducto != null && !this.idFacturaOrdenProducto.equals(other.idFacturaOrdenProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.FacturaOrdenProducto[ idFacturaOrdenProducto=" + idFacturaOrdenProducto + " ]";
    }
    
}
