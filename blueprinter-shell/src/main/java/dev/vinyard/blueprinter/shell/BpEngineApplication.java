package dev.vinyard.blueprinter.shell;

import dev.vinyard.blueprinter.autoconfigure.EnvironmentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EnvironmentProperties.class)
public class BpEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpEngineApplication.class);
    }

}
