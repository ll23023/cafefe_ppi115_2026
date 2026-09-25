package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "empleado", catalog = "pos_ppi115_2026", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Empleado.findAll", query = "SELECT e FROM Empleado e"),
    @NamedQuery(name = "Empleado.findByNombre", query = "SELECT e FROM Empleado e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Empleado.findByApellido", query = "SELECT e FROM Empleado e WHERE e.apellido = :apellido"),
    @NamedQuery(name = "Empleado.findByActivo", query = "SELECT e FROM Empleado e WHERE e.activo = :activo"),
    @NamedQuery(name = "Empleado.findByComentarios", query = "SELECT e FROM Empleado e WHERE e.comentarios = :comentarios")})
public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_empleado", nullable = false)
    //@Convert(converter = UUIDConverter.class)
    private UUID idEmpleado;
    @Size(max = 255)
    @Column(name = "nombre", length = 255)
    private String nombre;
    @Size(max = 255)
    @Column(name = "apellido", length = 255)
    private String apellido;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "comentarios", length = 2147483647)
    private String comentarios;
    @OneToMany(mappedBy = "idEmpleado")
    private List<EmpleadoRol> empleadoRolList;

    public Empleado() {
    }

    public Empleado(UUID idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public UUID getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(UUID idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public List<EmpleadoRol> getEmpleadoRolList() {
        return empleadoRolList;
    }

    public void setEmpleadoRolList(List<EmpleadoRol> empleadoRolList) {
        this.empleadoRolList = empleadoRolList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEmpleado != null ? idEmpleado.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empleado)) {
            return false;
        }
        Empleado other = (Empleado) object;
        if ((this.idEmpleado == null && other.idEmpleado != null) || (this.idEmpleado != null && !this.idEmpleado.equals(other.idEmpleado))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity.Empleado[ idEmpleado=" + idEmpleado + " ]";
    }
    
}
