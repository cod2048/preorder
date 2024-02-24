package com.hanghae.module_stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ModuleStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleStockApplication.class, args);
    }

}
