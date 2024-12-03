package dev.vinyard.bp;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dev.vinyard.bp.autoconfigure.EnvironmentProperties;
import dev.vinyard.bp.core.exception.BluePrinterException;
import dev.vinyard.bp.core.generation.GenerationManager;
import dev.vinyard.bp.core.prompt.Prompt;
import dev.vinyard.bp.core.prompt.PromptRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableConfigurationProperties({EnvironmentProperties.class})
@Mojo( name = "blueprint", defaultPhase = LifecyclePhase.PACKAGE )
public class BlueprinterRunnerMojo extends AbstractMojo {

    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    /**
     * Arguments that should be passed to the application.
     */
    @Parameter
    private String[] arguments;

    public void execute() throws MojoExecutionException {
        SpringApplication.run(BlueprinterRunnerMojo.class, Optional.ofNullable(arguments).orElseGet(() -> new String[0]));
    }

    @Bean
    public ApplicationRunner bluePrinterApplicationRunner(GenerationManager generationManager, PromptRepository promptRepository) {
        return args -> {
            args.getOptionNames().stream()
                    .map(key -> new Prompt(key, args.getOptionValues(key)))
                    .forEach(promptRepository::save);

            promptRepository.findAll().forEach(prompt -> getLog().info("Prompt: %s".formatted(prompt)));

            Optional.of(args).map(ApplicationArguments::getNonOptionArgs).filter(l -> !l.isEmpty()).map(List::getFirst).ifPresent(model -> {
                try {
                    generationManager.generate(model);
                } catch (BluePrinterException | IOException e) {
                    throw new RuntimeException(e);
                }
            });

            promptRepository.deleteAll();
        };
    }
}
