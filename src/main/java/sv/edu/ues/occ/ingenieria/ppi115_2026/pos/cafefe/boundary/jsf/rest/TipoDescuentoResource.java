package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.control.TipoDescuentoRepository;

@Path("tipo_descuento")
public class TipoDescuentoResource implements Serializable {
    
    @Inject
    TipoDescuentoRepository tdRepo;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
    @QueryParam("desde")
        @DefaultValue("0")
        int desde,
            
            @QueryParam("hasta")
        @DefaultValue("")
            int hasta) {
        
        try {
            return Response.ok(tdRepo.findRange(desde, hasta))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            
        } catch (Exception e) {
          Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
          
          return Response.status(500)
                  .header("App-Error", e.getMessage())
                  .build();
        }
    } 
        
}
