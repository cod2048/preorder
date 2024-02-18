package com.hanghae.module_item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class ModuleItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleItemApplication.class, args);
    }

}
