package pro.vinyard.vb.engine;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pro.vinyard.vb.engine.core.UserProperties;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.engine.core.environment.EnvironmentProperties;
import pro.vinyard.vb.engine.core.generation.GenerationManager;
import pro.vinyard.vb.engine.core.model.ModelManager;
import pro.vinyard.vb.engine.core.pluginManager.VbPluginManager;
import pro.vinyard.vb.engine.core.template.TemplateManager;

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
    public GenerationManager generationManager(ModelManager modelManager, VbPluginManager vbPluginManager, EnvironmentManager environmentManager) {
        return new GenerationManager(modelManager, vbPluginManager, environmentManager);
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
    public VbPluginManager vbPluginManager(EnvironmentManager environmentManager) {
        return new VbPluginManager(environmentManager);
    }
}
