package com.example.springai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

/**
 * api-docs: http://localhost:8080/api-docs
 * swagger-ui: http://localhost:8080/swagger-ui/index.html
 */
@Slf4j
@SpringBootApplication
public class SpringAiApplication {

	public static void main(String[] args) {
        // Must Include system environment variables when start the app
        // Get the value of the PATH environment variable
        String pathValue = System.getenv("path");
        Objects.requireNonNull(pathValue, "PATH environment variable not set on application");
        log.info("OS PATH: {}", pathValue);
    	SpringApplication.run(SpringAiApplication.class, args);
	}

}
