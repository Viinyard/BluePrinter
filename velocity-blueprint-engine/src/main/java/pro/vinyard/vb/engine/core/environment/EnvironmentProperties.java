package pro.vinyard.vb.engine.core.environment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "velocity-blueprint")
@Data
public class EnvironmentProperties {

    private Environment environment;

    @Data
    public static class Environment {

        private String workspaceDirectory;

        private String modelDirectory;

        private String templateDirectory;

    }
}
