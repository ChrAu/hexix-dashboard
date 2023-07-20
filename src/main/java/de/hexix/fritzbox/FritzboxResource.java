package de.hexix.fritzbox;


import de.hexix.Boundary;
import de.hexix.TemperaturFritz;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Path("/temperatures")
@Boundary
public class FritzboxResource{




@Inject
FritzboxService fritzboxService;



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TemperaturFritz> getTemperatures() {


       return fritzboxService.getTemperatur();
    }
}
