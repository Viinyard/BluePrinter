package dev.vinyard.bp.engine;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import dev.vinyard.bp.engine.core.UserProperties;
import dev.vinyard.bp.engine.core.environment.EnvironmentManager;
import dev.vinyard.bp.engine.core.environment.EnvironmentProperties;
import dev.vinyard.bp.engine.core.generation.GenerationManager;
import dev.vinyard.bp.engine.core.model.ModelManager;
import dev.vinyard.bp.engine.core.pluginManager.BpPluginManager;
import dev.vinyard.bp.engine.core.template.TemplateManager;

@AutoConfiguration
@EnableConfigurationProperties({EnvironmentProperties.class})
public class EngineAutoConfiguration {

    @Bean
    public EnvironmentManager environmentManager(EnvironmentProperties environmentProperties, UserProperties userProperties) {
        return new EnvironmentManager(environmentProperties, userProperties);
    }

    @Bean
    public UserProperties userProperties() {
        return new UserProperties();
    }

    @Bean
    public GenerationManager generationManager(ModelManager modelManager, BpPluginManager bpPluginManager, EnvironmentManager environmentManager) {
        return new GenerationManager(modelManager, bpPluginManager, environmentManager);
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
