package dev.vinyard.bp.autoconfigure;

import dev.vinyard.bp.core.environment.EnvironmentConfiguration;
import dev.vinyard.bp.core.environment.EnvironmentManager;
import dev.vinyard.bp.core.generation.GenerationManager;
import dev.vinyard.bp.core.model.ModelManager;
import dev.vinyard.bp.core.pluginManager.BpPluginManager;
import dev.vinyard.bp.core.prompt.DefaultPromptProvider;
import dev.vinyard.bp.core.prompt.PromptProvider;
import dev.vinyard.bp.core.prompt.PromptRepository;
import dev.vinyard.bp.core.support.DefaultUserProperties;
import dev.vinyard.bp.core.support.UserProperties;
import dev.vinyard.bp.core.template.TemplateManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.map.repository.config.EnableMapRepositories;

@AutoConfiguration
@EnableMapRepositories(basePackageClasses = PromptRepository.class)
public class BluePrinterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PromptProvider.class)
    public PromptProvider promptProvider(PromptRepository promptRepository) {
        return new DefaultPromptProvider(promptRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "blueprinter", name = "homeDirectory")
    public UserProperties inMemoryUserProperties(EnvironmentProperties environmentProperties) {
        UserProperties userProperties = new InMemoryUserProperties();
        userProperties.setProperty(UserProperties.HOME_DIRECTORY, environmentProperties.getHomeDirectory());
        return userProperties;
    }

    @Bean
    @ConditionalOnMissingBean(UserProperties.class)
    public UserProperties userProperties() {
        return new DefaultUserProperties();
    }

    @Bean
    public EnvironmentConfiguration environmentConfiguration(EnvironmentProperties environmentProperties) {
        return new EnvironmentConfiguration(environmentProperties.getWorkspaceDirectory(), environmentProperties.getModelDirectory(), environmentProperties.getTemplateDirectory());
    }

    @Bean
    public EnvironmentManager environmentManager(EnvironmentConfiguration environmentConfiguration, UserProperties userProperties) {
        return new EnvironmentManager(environmentConfiguration, userProperties);
    }

    @Bean
    public GenerationManager generationManager(ModelManager modelManager, BpPluginManager bpPluginManager, EnvironmentManager environmentManager, PromptProvider promptProvider) {
        return new GenerationManager(modelManager, bpPluginManager, environmentManager, promptProvider);
    }

    @Bean
    public ModelManager modelManager(EnvironmentManager environmentManager, TemplateManager templateManager) {
        return new ModelManager(environmentManager, templateManager);
    }

    @Bean
    public TemplateManager templateManager(EnvironmentManager environmentManager) {
        return new TemplateManager(environmentManager);
    }

    @Bean
    public BpPluginManager bpPluginManager(EnvironmentManager environmentManager) {
        return new BpPluginManager(environmentManager);
    }
}
