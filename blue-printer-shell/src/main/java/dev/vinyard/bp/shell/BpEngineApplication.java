package dev.vinyard.bp.shell;

import dev.vinyard.bp.engine.core.environment.EnvironmentProperties;
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
