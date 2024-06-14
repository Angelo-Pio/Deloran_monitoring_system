package com.tlc.deloran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableMongoRepositories
public class InterfaceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));
        SpringApplication.run(InterfaceApplication.class, args);

    }

}
