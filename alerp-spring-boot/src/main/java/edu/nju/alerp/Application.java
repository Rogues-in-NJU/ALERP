package edu.nju.alerp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
public class Application {

    @CrossOrigin(origins = {"http://localhost:8080", "null"}, allowCredentials = "true", allowedHeaders = "", methods = {})
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
