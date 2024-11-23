package dev.vinyard.bp.runner;

import dev.vinyard.bp.core.environment.EnvironmentManager;
import dev.vinyard.bp.core.generation.GenerationManager;
import dev.vinyard.bp.core.prompt.Prompt;
import dev.vinyard.bp.core.prompt.PromptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

@Slf4j
public class BluePrinterApplicationRunner implements ApplicationRunner {

    private final GenerationManager generationManager;

    private final PromptRepository promptRepository;

    public BluePrinterApplicationRunner(GenerationManager generationManager, EnvironmentManager environmentManager, PromptRepository promptRepository) {
        this.generationManager = generationManager;
        this.promptRepository = promptRepository;
    }

    @Override
    public void run(ApplicationArguments arguments) throws Exception {
        List<Prompt> prompts = arguments.getOptionNames().stream()
                .map(key -> new Prompt(key, arguments.getOptionValues(key)))
                .toList();

        promptRepository.saveAll(prompts);

        promptRepository.findAll().forEach(p -> log.info("Prompt: {}", p));

        generationManager.generate("antlr-solution");

        promptRepository.deleteAll();
    }
}
