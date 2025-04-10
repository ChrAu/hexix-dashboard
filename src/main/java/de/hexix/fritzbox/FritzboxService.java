package de.hexix.fritzbox;

import de.hexix.Control;
import de.hexix.TemperaturFritz;
import de.hexix.fritzbox.model.homeautomation.Device;
import de.hexix.fritzbox.model.homeautomation.Temperature;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


//    @ConfigProperty(name = "elasticsearch.apiKey.key")
//    String apiKey;
//
//    @ConfigProperty(name = "quarkus.elasticsearch.hosts")
//    String esHost;
//
//    @ConfigProperty(name = "quarkus.elasticsearch.port")
//    String esPort;

    private final String indexName = "temperatur";



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

    @Transactional
    public void uploadElasticSearch() {


        final TypedQuery<TemperaturFritz> namedQuery = entityManager.createNamedQuery(TemperaturFritz.findNotElasticSearchPersisted, TemperaturFritz.class);
        namedQuery.setMaxResults(1000);
        final List<TemperaturFritz> notInElasticSearch = namedQuery.getResultList();

        final ArrayList<JsonObject> entityList = new ArrayList<>();
        for (TemperaturFritz temperaturFritz : notInElasticSearch) {

            entityList.add(new JsonObject().put("index", new JsonObject().put("_index", indexName).put("_id", temperaturFritz.id)));
            entityList.add(JsonObject.mapFrom(temperaturFritz));
        }

//        Request request = new Request(
//                "POST", "temperatur/_bulk?pretty");
//        final String ndJsonString = toNdJsonString(entityList);
//        request.setEntity(new StringEntity(
//                ndJsonString,
//                ContentType.create("application/x-ndjson")));
//
//        final RestClientBuilder builder = RestClient.builder(new HttpHost("es.codeheap.dev", 443, "https"));
//        Header[] defaultHeaders =
//                new Header[]{new BasicHeader("Authorization",
//                        "ApiKey " + apiKey)};
//
//        builder.setDefaultHeaders(defaultHeaders);
//        builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
//            try {
//                return httpAsyncClientBuilder.setSSLContext(SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
//            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
//                LOG.log(Level.WARNING, e.getMessage(), e);
//                throw new RuntimeException(e);
//            }
//        });
//
//
//        try (RestClient client = builder.build()){
//            client.performRequest(request);
//
//
//
//        } catch (IOException e) {
//            LOG.log(Level.WARNING, e.getMessage(), e);
//            throw new RuntimeException(e);
//        }

        notInElasticSearch.forEach(temperaturFritz -> temperaturFritz.setPersistedElasticSearch(true));

    }

    private static String toNdJsonString(List<JsonObject> objects) {
        return objects.stream()
                .map(JsonObject::encode)
                .collect(Collectors.joining("\n", "", "\n"));
    }
}
