package de.hexix;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class DurationLogger implements AutoCloseable {

    private final String methode;
    private final boolean logOnlyGt10;
    private Instant start;
    private Logger LOG;

    public DurationLogger(Class aClass, String methode){
        this(aClass, methode, false);
    }

    public DurationLogger(Class aClass, String methode, boolean logOnlyGt10){
        this.methode = methode;
        LOG = Logger.getLogger(aClass.getName());
        start = Instant.now();
        this.logOnlyGt10 = logOnlyGt10;
    }


    @Override
    public void close() {
        final long l = Duration.between(start, Instant.now()).toMillis();
        if(!logOnlyGt10 || l >10) {
            LOG.info("Method: " + methode + ", Request duration: " + l + "ms");
        }
    }
}
