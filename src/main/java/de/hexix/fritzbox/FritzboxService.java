package de.hexix.fritzbox;


import de.hexix.fritzbox.model.homeautomation.Device;
import de.hexix.fritzbox.model.homeautomation.Temperature;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/temperatures")

public class FritzboxService {

    @ConfigProperty(name = "fritzboxusername")
     String username;
    @ConfigProperty(name = "fritzboxpassword")
     String password;
    @ConfigProperty(name = "fritzboxUrl")
     String fritzboxUrl;







    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTemperatures() {


        HomeAutomation connect = HomeAutomation.connect(fritzboxUrl, username, password);
        List<Device> devices = connect.getDeviceListInfos().getDevices();
        HashMap<String, String> temperaturGeraet = new HashMap<>();
        for (Device device : devices) {
            Temperature temperature = device.getTemperature();
            temperaturGeraet.put(device.getName(), temperature.getCelsius());
        }


        return "TEST";
    }
}
