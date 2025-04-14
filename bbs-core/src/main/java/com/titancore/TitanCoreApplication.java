package com.titancore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TitanCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(TitanCoreApplication.class,args);
    }
}
