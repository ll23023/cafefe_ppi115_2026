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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author mikelb
 */
@Entity
@Table(name = "empleado_rol", catalog = "pos_ppi115_2026", schema = "public")
@NamedQueries({
    @NamedQuery(name = "EmpleadoRol.findAll", query = "SELECT e FROM EmpleadoRol e"),
    @NamedQuery(name = "EmpleadoRol.findByActivo", query = "SELECT e FROM EmpleadoRol e WHERE e.activo = :activo"),
    @NamedQuery(name = "EmpleadoRol.findByObservaciones", query = "SELECT e FROM EmpleadoRol e WHERE e.observaciones = :observaciones")})
public class EmpleadoRol implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_empleado_rol", nullable = false)
    @Convert(converter = UUIDConverter.class)
    private UUID idEmpleadoRol;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "observaciones", length = 2147483647)
    private String observaciones;
    @JoinColumn(name = "id_empleado", referencedColumnName = "id_empleado")
    @ManyToOne
    private Empleado idEmpleado;
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")
    @ManyToOne
    private Rol idRol;
    @OneToMany(mappedBy = "idEmpleadoRol")
    private List<Factura> facturaList;
    @OneToMany(mappedBy = "idEmpleadoRol")
    private List<Orden> ordenList;

    public EmpleadoRol() {
    }

    public EmpleadoRol(UUID idEmpleadoRol) {
        this.idEmpleadoRol = idEmpleadoRol;
    }

    public UUID getIdEmpleadoRol() {
        return idEmpleadoRol;
    }

    public void setIdEmpleadoRol(UUID idEmpleadoRol) {
        this.idEmpleadoRol = idEmpleadoRol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Empleado getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Empleado idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
    }

    public List<Orden> getOrdenList() {
        return ordenList;
    }

    public void setOrdenList(List<Orden> ordenList) {
        this.ordenList = ordenList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEmpleadoRol != null ? idEmpleadoRol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmpleadoRol)) {
            return false;
        }
        EmpleadoRol other = (EmpleadoRol) object;
        if ((this.idEmpleadoRol == null && other.idEmpleadoRol != null) || (this.idEmpleadoRol != null && !this.idEmpleadoRol.equals(other.idEmpleadoRol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.EmpleadoRol[ idEmpleadoRol=" + idEmpleadoRol + " ]";
    }
    
}
