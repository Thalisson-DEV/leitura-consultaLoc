package br.sipel.leituraconsultaloc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LeituraConsultaLocApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeituraConsultaLocApplication.class, args);
    }

}
