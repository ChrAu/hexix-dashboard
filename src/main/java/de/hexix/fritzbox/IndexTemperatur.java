package de.hexix.fritzbox;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/temperatur")
@RegisterRestClient
public interface IndexTemperatur {

    @POST
    @Path("/_doc/{id}")
    RestResponse<String> index(@PathParam("id") String id, String body) ;

}
