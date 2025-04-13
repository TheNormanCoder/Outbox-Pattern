package com.example.outbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Classe principale per l'avvio dell'applicazione Spring Boot
 * che implementa il Pattern Outbox.
 *
 * L'annotazione @EnableScheduling abilita l'esecuzione di metodi
 * schedulati come il polling degli eventi dall'outbox.
 *
 * L'annotazione @EnableTransactionManagement abilita la gestione 
 * delle transazioni dichiarative per garantire l'atomicità delle 
 * operazioni di salvataggio dati e eventi.
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class OutboxPatternApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutboxPatternApplication.class, args);
    }
}