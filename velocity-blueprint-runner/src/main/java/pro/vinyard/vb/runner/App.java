package pro.vinyard.vb.runner;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.engine.core.environment.EnvironmentProperties;
import pro.vinyard.vb.engine.core.generation.GenerationManager;

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
