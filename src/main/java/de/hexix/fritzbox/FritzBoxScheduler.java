package de.hexix.fritzbox;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class FritzBoxScheduler {
    private static final Logger LOG = Logger.getLogger(FritzBoxScheduler.class.getName());


    @Inject
    FritzboxService fritzboxService;



    @Scheduled(cron = "{de.hexix.dashboard.fritz.cron}")
    void checkFritzboxData(){
        fritzboxService.getTemperatur();

        try {
            fritzboxService.uploadElasticSearch();
        }catch (Exception e){
            LOG.warning(e.getMessage());
        }

    }





}
