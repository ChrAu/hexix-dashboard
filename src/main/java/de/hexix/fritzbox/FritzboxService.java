package de.hexix.fritzbox;

import de.hexix.Control;
import de.hexix.TemperaturFritz;
import de.hexix.fritzbox.model.homeautomation.Device;
import de.hexix.fritzbox.model.homeautomation.Temperature;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Control
public class FritzboxService {
    private static final Logger LOG = Logger.getLogger(FritzboxService.class.getName());


    @ConfigProperty(name = "fritzboxusername")
    String username;
    @ConfigProperty(name = "fritzboxpassword")
    String password;
    @ConfigProperty(name = "fritzboxUrl")
    String fritzboxUrl;
    @Transactional
    public List<TemperaturFritz> getTemperatur(){
        HomeAutomation connect = HomeAutomation.connect(fritzboxUrl, username, password);
        List<Device> devices = connect.getDeviceListInfos().getDevices();
        HashMap<String, String> temperaturGeraet = new HashMap<>();
        List<TemperaturFritz> fritzVentil = new ArrayList<>();
        for (Device device : devices) {
            Temperature temperature = device.getTemperature();
            temperaturGeraet.put(device.getName(), temperature.getCelsius());
            final TemperaturFritz temperaturFritz = new TemperaturFritz();
            temperaturFritz.setLocation(device.getName());
            temperaturFritz.setTemperatur(Double.parseDouble(temperature.getCelsius()));
            temperaturFritz.setDevice(device.getIdentifier());
            temperaturFritz.persist();
            fritzVentil.add(temperaturFritz);
        }

        LOG.info(String.format("Temperatur: %s" , convertWithIteration(temperaturGeraet)));

        return fritzVentil;
    }

    private String convertWithIteration(Map<String, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            mapAsString.append(key + "=" + map.get(key) + ", ");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }
}
