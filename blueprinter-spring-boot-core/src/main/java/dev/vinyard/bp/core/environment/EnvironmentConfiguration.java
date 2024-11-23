package dev.vinyard.bp.core.environment;

/**
 * @param workspaceDirectory Root directory of the workspace, where the BluePrinter project folder is located.
 * @param modelDirectory Directory where the models are located.
 * @param templateDirectory Directory where the templates are located.
 */
public record EnvironmentConfiguration(String workspaceDirectory, String modelDirectory, String templateDirectory) { }
