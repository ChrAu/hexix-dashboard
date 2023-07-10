package de.hexix.fritzbox;

import de.hexix.Control;
import de.hexix.fritzbox.model.homeautomation.Device;
import de.hexix.fritzbox.model.homeautomation.Temperature;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class FritzBoxScheduler {
    private static final Logger LOG = Logger.getLogger(FritzBoxScheduler.class.getName());

    @ConfigProperty(name = "fritzboxusername")
    String username;
    @ConfigProperty(name = "fritzboxpassword")
    String password;
    @ConfigProperty(name = "fritzboxUrl")
    String fritzboxUrl;


    @Scheduled(cron = "{de.hexix.dashboard.fritz.cron}")
    void checkFritzboxData(){


        HomeAutomation connect = HomeAutomation.connect(fritzboxUrl, username, password);
        List<Device> devices = connect.getDeviceListInfos().getDevices();
        Map<String, String> temperaturGeraet = new HashMap<>();
        for (Device device : devices) {
            Temperature temperature = device.getTemperature();
            temperaturGeraet.put(device.getName(), temperature.getCelsius());
        }

        LOG.info(String.format("Temperatur: %s" , convertWithIteration(temperaturGeraet)));

    }

    public String convertWithIteration(Map<String, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            mapAsString.append(key + "=" + map.get(key) + ", ");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }

}
