package com.fwhamood.tn.soasydessaipoc;

import config.VectorStoreMockConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(VectorStoreMockConfig.class) // Explicitly registers your mock VectorStore bean
public class SoasyDessAiPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoasyDessAiPocApplication.class, args);
    }

}
