package com.tlc.deloran;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableMongoRepositories
public class InterfaceApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(InterfaceApplication.class, args);

        String timezone = context.getBean(InterfaceApplication.class).env.getProperty("deloran.env.timezone");
        if (timezone != null) {
            TimeZone.setDefault(TimeZone.getTimeZone(timezone));
            System.out.println("Default Time Zone set to: " + timezone);
        } else {
            System.err.println("Could not set default time zone. Property 'app.timezone' not found.");
        }
    }

}
