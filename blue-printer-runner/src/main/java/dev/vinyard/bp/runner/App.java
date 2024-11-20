package dev.vinyard.bp.runner;

import dev.vinyard.bp.engine.core.environment.EnvironmentManager;
import dev.vinyard.bp.engine.core.environment.EnvironmentProperties;
import dev.vinyard.bp.engine.core.generation.GenerationManager;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(EnvironmentProperties.class)
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(GenerationManager generationManager, EnvironmentManager environmentManager) {
        return new BluePrinterApplicationRunner(generationManager, environmentManager);
    }
}
