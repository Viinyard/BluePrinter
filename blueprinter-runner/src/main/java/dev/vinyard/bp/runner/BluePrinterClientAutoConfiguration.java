package dev.vinyard.bp.runner;

import dev.vinyard.bp.autoconfigure.EnvironmentProperties;
import dev.vinyard.bp.core.environment.EnvironmentManager;
import dev.vinyard.bp.core.generation.GenerationManager;
import dev.vinyard.bp.core.prompt.PromptRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({EnvironmentProperties.class})
public class BluePrinterClientAutoConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(BluePrinterClientAutoConfiguration.class, args);
    }

    @Bean
    public ApplicationRunner bluePrinterApplicationRunner(GenerationManager generationManager, EnvironmentManager environmentManager, PromptRepository promptRepository) {
        return new BluePrinterApplicationRunner(generationManager, environmentManager, promptRepository);
    }
}
