package dev.vinyard.bp.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blueprinter")
public class EnvironmentProperties {

    /**
     * Name of the workspace directory
     */
    private String workspaceDirectory = "BluePrinter";

    /**
     * Directory where the workspace is located
     */
    private String homeDirectory;

    /**
     * Name of the model directory
     */
    private String modelDirectory = "models";

    /**
     * Name of the template directory
     */
    private String templateDirectory = "templates";

    public String getWorkspaceDirectory() {
        return workspaceDirectory;
    }

    public void setWorkspaceDirectory(String workspaceDirectory) {
        this.workspaceDirectory = workspaceDirectory;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public String getModelDirectory() {
        return modelDirectory;
    }

    public void setModelDirectory(String modelDirectory) {
        this.modelDirectory = modelDirectory;
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }
}
