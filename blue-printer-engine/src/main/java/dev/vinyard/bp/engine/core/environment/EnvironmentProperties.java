package dev.vinyard.bp.engine.core.environment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blue-printer")
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
