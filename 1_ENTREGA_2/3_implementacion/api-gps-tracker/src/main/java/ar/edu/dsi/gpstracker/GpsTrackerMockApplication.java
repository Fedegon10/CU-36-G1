package ar.edu.dsi.gpstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ar.edu.dsi.gpstracker.config.GpsApiProperties;

// Inicia la API simulada del GPS Tracker (puerto 8080)
@SpringBootApplication
@EnableConfigurationProperties(GpsApiProperties.class)
public class GpsTrackerMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(GpsTrackerMockApplication.class, args);
    }
}
