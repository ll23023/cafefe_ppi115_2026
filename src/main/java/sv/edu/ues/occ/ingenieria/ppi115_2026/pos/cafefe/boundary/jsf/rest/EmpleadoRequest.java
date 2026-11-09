package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import java.util.ArrayList;
import java.util.List;

public class EmpleadoRequest {
    public String nombre;
    public String apellido;
    public Boolean activo;
    public String comentarios;
    public List<String> roles = new ArrayList<>();
}