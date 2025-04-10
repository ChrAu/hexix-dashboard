package de.hexix;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "temperatur_fritz")
@NamedQueries(@NamedQuery(name = TemperaturFritz.findNotElasticSearchPersisted, query = "select t from TemperaturFritz t where t.persistedElasticSearch = false"))
public class TemperaturFritz extends PanacheEntity {

    public final static String findNotElasticSearchPersisted = "TemperaturFritz.findNotElasticSearchPersisted";


    private String location;
    private Double temperatur;
    private String device;
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    @Column(nullable = false)
    private boolean persistedElasticSearch = false;


    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public Double getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(final Double temperatur) {
        this.temperatur = temperatur;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(final String device) {
        this.device = device;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


    public boolean isPersistedElasticSearch() {
        return persistedElasticSearch;
    }

    public void setPersistedElasticSearch(final boolean persistedElasticSearch) {
        this.persistedElasticSearch = persistedElasticSearch;
    }


}
