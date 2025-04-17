package de.hexix.fritzbox;

import de.hexix.Control;
import de.hexix.TemperaturFritz;
import de.hexix.fritzbox.model.homeautomation.Device;
import de.hexix.fritzbox.model.homeautomation.Temperature;
import io.quarkus.restclient.runtime.QuarkusRestClientBuilder;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
// Import für EntityBuilder
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
// Import für UTF_8
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Control
public class FritzboxService {
    private static final Logger LOG = Logger.getLogger(FritzboxService.class.getName());


    @Inject
    EntityManager entityManager;

    @ConfigProperty(name = "fritzboxusername")
    String username;

    @ConfigProperty(name = "fritzboxpassword")
    String password;

    @ConfigProperty(name = "fritzboxUrl")
    String fritzboxUrl;


    @ConfigProperty(name = "elasticsearch.apiKey.key")
    String apiKey;


    private final String indexName = "temperatur";



    @Transactional
    public List<TemperaturFritz> getTemperatur(){
        HomeAutomation connect = HomeAutomation.connect(fritzboxUrl, username, password);
        List<Device> devices = connect.getDeviceListInfos().getDevices();
        HashMap<String, String> temperaturGeraet = new HashMap<>();
        List<TemperaturFritz> fritzVentil = new ArrayList<>();
        for (Device device : devices) {
            Temperature temperature = device.getTemperature();
            double tempValue;
            try {
                tempValue = Double.parseDouble(temperature.getCelsius());
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "Konnte Temperatur nicht parsen für Gerät " + device.getName() + ": " + temperature.getCelsius(), e);
                continue;
            }
            temperaturGeraet.put(device.getName(), temperature.getCelsius());
            final TemperaturFritz temperaturFritz = new TemperaturFritz();
            temperaturFritz.setLocation(device.getName().trim());
            temperaturFritz.setTemperatur(tempValue);
            temperaturFritz.setDevice(device.getIdentifier());
            fritzVentil.add(temperaturFritz);
        }

        if (!fritzVentil.isEmpty()) {
            fritzVentil.forEach(tf -> entityManager.persist(tf));
            LOG.info(fritzVentil.size() + " Temperatur-Datensätze zum Persistieren vorbereitet.");
        }

        LOG.info(String.format("Temperatur: %s" , convertWithIteration(temperaturGeraet)));
        return fritzVentil;
    }



    private String convertWithIteration(Map<String, ?> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @Transactional
    public void uploadElasticSearch() {

        final TypedQuery<TemperaturFritz> namedQuery = entityManager.createNamedQuery(TemperaturFritz.findNotElasticSearchPersisted, TemperaturFritz.class);
        namedQuery.setMaxResults(1000); // Nur ein Dokument holen
        final List<TemperaturFritz> notInElasticSearch = namedQuery.getResultList();

        final IndexTemperatur build = QuarkusRestClientBuilder.newBuilder()
                .baseUri(URI.create("https://es.codeheap.dev"))
                .clientHeadersFactory(new ClientHeadersFactory() {
                    @Override
                    public MultivaluedMap<String, String> update(final MultivaluedMap<String, String> incomingHeaders, final MultivaluedMap<String, String> clientOutgoingHeaders) {
                        return null;
                    }

                    @Override
                    public Uni<MultivaluedMap<String, String>> getHeaders(final MultivaluedMap<String, String> incomingHeaders, final MultivaluedMap<String, String> clientOutgoingHeaders) {
                        return Uni.createFrom().item(() -> {
                            MultivaluedHashMap<String, String> newHeaders = new MultivaluedHashMap<>();
                            newHeaders.add("Authorization", "ApiKey " + apiKey);
                            return newHeaders;
                        });
                    }
                })
                .build(IndexTemperatur.class);


        for (TemperaturFritz inElasticSearch : notInElasticSearch) {

            try(final RestResponse<String> index = build.index(String.valueOf(6), JsonObject.mapFrom(inElasticSearch).encode())){
                if(index.getStatus() == 200){
                    inElasticSearch.setPersistedElasticSearch(true);
                }
            }

        }


    }


    // Die toNdJsonString Methode wird für _create nicht benötigt, kann aber bleiben,
    // falls du sie für den Bulk-Upload wieder brauchst.
    private static String toNdJsonString(List<JsonObject> objects) {
        return objects.stream()
                .map(JsonObject::encode)
                .collect(Collectors.joining("\n", "", "\n"));
    }
}

