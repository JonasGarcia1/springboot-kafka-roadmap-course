package com.interviewlab.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la primera práctica. @SpringBootApplication reúne configuración,
 * autoconfiguración y escaneo de componentes desde este paquete hacia abajo.
 */
@SpringBootApplication
public class LearningApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LearningApiApplication.class, args);
    }
}
