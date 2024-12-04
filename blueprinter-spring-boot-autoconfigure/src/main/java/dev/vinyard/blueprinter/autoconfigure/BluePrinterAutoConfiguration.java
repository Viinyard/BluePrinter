package dev.vinyard.blueprinter.autoconfigure;

import dev.vinyard.blueprinter.core.environment.EnvironmentConfiguration;
import dev.vinyard.blueprinter.core.environment.EnvironmentManager;
import dev.vinyard.blueprinter.core.generation.GenerationManager;
import dev.vinyard.blueprinter.core.model.ModelManager;
import dev.vinyard.blueprinter.core.pluginManager.BpPluginManager;
import dev.vinyard.blueprinter.core.prompt.DefaultPromptProvider;
import dev.vinyard.blueprinter.core.prompt.PromptProvider;
import dev.vinyard.blueprinter.core.prompt.PromptRepository;
import dev.vinyard.blueprinter.core.support.DefaultUserProperties;
import dev.vinyard.blueprinter.core.support.UserProperties;
import dev.vinyard.blueprinter.core.template.TemplateManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.map.repository.config.EnableMapRepositories;

/**
 * Auto configuration for BluePrinter.
 */
@AutoConfiguration
@EnableMapRepositories(basePackageClasses = PromptRepository.class)
public class BluePrinterAutoConfiguration {

    /**
     * Create a new prompt provider with the prompt repository if no other prompt provider is defined.
     * You can use the prompt repository to save and retrieve prompts.
     * @param promptRepository The prompt repository
     * @return Prompt provider
     */
    @Bean
    @ConditionalOnMissingBean(PromptProvider.class)
    public PromptProvider promptProvider(PromptRepository promptRepository) {
        return new DefaultPromptProvider(promptRepository);
    }

    /**
     * Create a new in memory user properties initialized with the home directory from the environment properties.
     * @param environmentProperties The environment properties
     * @return In memory user properties initialized with the home directory from the environment properties
     */
    @Bean
    @ConditionalOnProperty(prefix = "blueprinter", name = "homeDirectory")
    public UserProperties inMemoryUserProperties(EnvironmentProperties environmentProperties) {
        UserProperties userProperties = new InMemoryUserProperties();
        userProperties.setProperty(UserProperties.HOME_DIRECTORY, environmentProperties.getHomeDirectory());
        return userProperties;
    }

    /**
     * Create a new default user properties if no other user properties are defined.
     * @return Default user properties
     */
    @Bean
    @ConditionalOnMissingBean(UserProperties.class)
    public UserProperties userProperties() {
        return new DefaultUserProperties();
    }

    /**
     * Create a new environment configuration for the environmentManager. The configuration is initialized with the workspace directory, model directory and template directory from the environment properties.
     * @param environmentProperties The environment properties
     * @return Environment configuration initialized with the workspace directory, model directory and template directory from the environment properties
     */
    @Bean
    public EnvironmentConfiguration environmentConfiguration(EnvironmentProperties environmentProperties) {
        return new EnvironmentConfiguration(environmentProperties.getWorkspaceDirectory(), environmentProperties.getModelDirectory(), environmentProperties.getTemplateDirectory());
    }

    /**
     * Create a new environment manager with the environment configuration and user properties.
     * @param environmentConfiguration environment configuration
     * @param userProperties user properties
     * @return Environment manager
     */
    @Bean
    public EnvironmentManager environmentManager(EnvironmentConfiguration environmentConfiguration, UserProperties userProperties) {
        return new EnvironmentManager(environmentConfiguration, userProperties);
    }

    /**
     * Create a new generation manager with the model manager, plugin manager, environment manager and prompt provider.
     * @param modelManager model manager used to find the model that gave prompts to the prompt provider and directives to generate
     * @param bpPluginManager plugin manager used to initialize the plugins for the velocity context
     * @param environmentManager environment manager used to find the home directory
     * @param promptProvider prompt provider used to get the prompts for the model
     * @return Generation manager
     * @see GenerationManager#generate(String)
     */
    @Bean
    public GenerationManager generationManager(ModelManager modelManager, BpPluginManager bpPluginManager, EnvironmentManager environmentManager, PromptProvider promptProvider) {
        return new GenerationManager(modelManager, bpPluginManager, environmentManager, promptProvider);
    }

    /**
     * Create a new model manager with the environment manager and template manager.
     * @param environmentManager environment manager used to find the model directory
     * @param templateManager template manager used to find the templates linked to the model
     * @return Model manager
     */
    @Bean
    public ModelManager modelManager(EnvironmentManager environmentManager, TemplateManager templateManager) {
        return new ModelManager(environmentManager, templateManager);
    }

    /**
     * Create a new template manager with the environment manager.
     * @param environmentManager environment manager used to find the template directory
     * @return Template manager
     */
    @Bean
    public TemplateManager templateManager(EnvironmentManager environmentManager) {
        return new TemplateManager(environmentManager);
    }

    /**
     * Create a new blueprinter plugin manager for the velocity context.
     * @param environmentManager environment manager used to find the plugin directory
     * @return Blueprinter plugin manager
     */
    @Bean
    public BpPluginManager bpPluginManager(EnvironmentManager environmentManager) {
        return new BpPluginManager(environmentManager);
    }
}
