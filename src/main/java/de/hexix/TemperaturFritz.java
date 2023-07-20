package de.hexix;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name="temperatur_fritz")
public class TemperaturFritz extends PanacheEntity {

    private String location;
    private Double temperatur;
    private String device;
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();


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
}
