package ar.edu.utn.dsi.bolsines;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Inicia el backend del sistema (puerto 8081)
@SpringBootApplication
public class BolsinesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BolsinesApplication.class, args);
    }
}
