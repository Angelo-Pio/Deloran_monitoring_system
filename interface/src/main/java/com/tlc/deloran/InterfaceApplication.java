package com.tlc.deloran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class InterfaceApplication {

    public static void main(String[] args) {

        SpringApplication.run(InterfaceApplication.class, args);


    }

}
