package com.ocs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OfficeApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeApplication.class, args);
    }
}
