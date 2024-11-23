package dev.vinyard.bp.core.environment;

import dev.vinyard.bp.core.environment.entities.Environment;
import dev.vinyard.bp.core.exception.BluePrinterException;
import dev.vinyard.bp.core.support.UserProperties;
import dev.vinyard.bp.core.utils.DirectoryUtils;
import dev.vinyard.bp.core.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Manager for the environment.
 * <p>Handle the initialization of the environment, the loading of the configuration file and the management of the home directory.</p>
 */
@Slf4j
public class EnvironmentManager {

    public static final String FILE_LOCATION = "config/environment/";
    public static final String SCHEMA_FILE = "environment.xsd";
    public static final String CONFIGURATION_FILE = "environment.xml";

    public static final String PLUGIN_DIRECTORY = "plugins";

    private final ApplicationHome home;

    private final EnvironmentConfiguration environmentConfiguration;

    private final UserProperties userProperties;

    @Getter
    private File homeDirectory;

    public EnvironmentManager(EnvironmentConfiguration environmentConfiguration, UserProperties userProperties) {
        this.environmentConfiguration = environmentConfiguration;
        this.userProperties = userProperties;
        this.home = new ApplicationHome(EnvironmentManager.class);
    }

    /**
     * Initialize the environment manager.
     * <p>If the home directory is set in the user properties, it will be used as the home directory.</p>
     */
    @PostConstruct
    public void init() {
        Optional.ofNullable(userProperties.getProperty(UserProperties.HOME_DIRECTORY)).map(Path::of).ifPresent(this::setHomeDirectory);
    }

    public String getLineSeparator() {
        return System.lineSeparator();
    }

    public String getWorkingDirectory() {
        return DirectoryUtils.getWorkingDirectory();
    }

    public String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    /**
     * Set the home directory.
     * <p>The home directory must exist and be a directory.</p>
     * <p>The home directory will be set in the user properties.</p>
     *
     * @param homeDirectory The home directory Path to set
     * @throws IllegalArgumentException if the home directory is null, does not exist or is not a directory
     */
    public void setHomeDirectory(Path homeDirectory) {
        try {
            File homeDirectoryFile = Optional.of(homeDirectory).map(Path::toFile).orElseThrow(() -> new IllegalArgumentException("Home directory cannot be null"));

            homeDirectoryFile = homeDirectoryFile.getCanonicalFile();

            if (!homeDirectoryFile.exists())
                throw new IllegalArgumentException("Home directory does not exist");
            if (!homeDirectoryFile.isDirectory())
                throw new IllegalArgumentException("Home directory is not a directory");

            this.homeDirectory = homeDirectoryFile;

            this.userProperties.setProperty(UserProperties.HOME_DIRECTORY, this.homeDirectory.getAbsolutePath());

            log.info("Home directory set to {}", this.homeDirectory.getAbsolutePath());
        } catch (Exception e) {
            throw new IllegalArgumentException("Home directory is not valid");
        }
    }

    public String getJavaVersion() {
        return System.getProperty("java.runtime.version");
    }

    public File getJarFile() {
        return home.getSource();
    }

    public String getJarFileLocation() {
        return Optional.ofNullable(getJarFile()).map(File::getAbsolutePath).orElse(null);
    }

    public File getConfigurationFile() {
        return new File(getWorkspaceDirectory(), CONFIGURATION_FILE);
    }

    public boolean checkConfigurationFile() {
        log.info("Check configuration file, configuration file is {}", getConfigurationFile());
        return Optional.ofNullable(this.getConfigurationFile()).filter(File::exists).filter(File::isFile).isPresent();
    }

    public File getSchemaFile() {
        return new File(getWorkspaceDirectory(), SCHEMA_FILE);
    }

    public boolean checkSchemaFile() {
        log.info("Check schema file, schema file is {}", getSchemaFile());
        return Optional.ofNullable(this.getSchemaFile()).filter(File::exists).filter(File::isFile).isPresent();
    }

    public File getWorkspaceDirectory() {
        return new File(homeDirectory, environmentConfiguration.workspaceDirectory());
    }

    public boolean checkWorkspaceDirectory() {
        log.info("Check workspace directory, workspace directory is {}", getWorkspaceDirectory());
        return Optional.ofNullable(this.getWorkspaceDirectory()).filter(File::exists).filter(File::isDirectory).isPresent();
    }

    public File getModelDirectory() {
        return new File(getWorkspaceDirectory(), environmentConfiguration.modelDirectory());
    }

    public boolean checkModelDirectory() {
        log.info("Check model directory, model directory is {}", getModelDirectory());
        return Optional.ofNullable(this.getModelDirectory()).filter(File::exists).filter(File::isDirectory).isPresent();
    }

    public File getTemplateDirectory() {
        return new File(getWorkspaceDirectory(), environmentConfiguration.templateDirectory());
    }

    public boolean checkTemplateDirectory() {
        log.info("Check template directory, template directory is {}", getTemplateDirectory());
        return Optional.ofNullable(this.getTemplateDirectory()).filter(File::exists).filter(File::isDirectory).isPresent();
    }

    /**
     * Initialize the environment.
     * <p>The workspace is initialized in the home directory by :</p>
     * <ul>
     *      <li>Creating the workspace directory, named {@link EnvironmentConfiguration#workspaceDirectory()}</li>
     *      <li>Creating the model directory in the workspace, named {@link EnvironmentConfiguration#modelDirectory()}</li>
     *      <li>Creating the template directory in the workspace, named {@link EnvironmentConfiguration#templateDirectory()}</li>
     *      <li>Creating the {@code environment.xml} file and his validator {@code environment.xsd} file in the workspace.</li>
     *      <li>Creating the plugin directory in the workspace, named {@code plugins}</li>
     * </ul>
     */
    public void initialize() {
        if (!this.checkWorkspaceDirectory()) {
            File workspaceDirectory = DirectoryUtils.createFolder(getWorkspaceDirectory());
            log.info("Workspace directory {} created.", workspaceDirectory.getAbsolutePath());
        }

        if (!this.checkModelDirectory()) {
            File modelDirectory = DirectoryUtils.createFolder(getModelDirectory());
            log.info("Model directory {} created.", modelDirectory.getAbsolutePath());
        }

        if (!this.checkTemplateDirectory()) {
            File templateDirectory = DirectoryUtils.createFolder(getTemplateDirectory());
            log.info("Template directory {} created.", templateDirectory.getAbsolutePath());
        }

        if (!this.checkSchemaFile()) {
            File schemaFile = FileUtils.copyFile(FILE_LOCATION + SCHEMA_FILE, getSchemaFile());
            log.info("Environment schema file {} created.", schemaFile.getAbsolutePath());
        }

        if (!this.checkConfigurationFile()) {
            File configurableFile = FileUtils.copyFile(FILE_LOCATION + CONFIGURATION_FILE, getConfigurationFile());
            log.info("Environment configuration file {} created.", configurableFile.getAbsolutePath());
        }

        if (!this.checkPluginDirectory()) {
            File pluginDirectory = DirectoryUtils.createFolder(getPluginDirectory());
            log.info("Plugin directory {} created.", pluginDirectory.getAbsolutePath());
        }

        try {
            Environment environment = loadConfigurationFile();
            if (environment == null)
                throw new BluePrinterException("Environment configuration file is null");
        } catch (BluePrinterException e) {
            log.error("Environment configuration file {} is invalid : {}", getConfigurationFile().getAbsolutePath(), e.getMessage());
        }
    }

    public File getPluginDirectory() {
        return new File(getWorkspaceDirectory(), PLUGIN_DIRECTORY);
    }

    public boolean checkPluginDirectory() {
        return Optional.ofNullable(this.getPluginDirectory()).filter(File::exists).filter(File::isDirectory).isPresent();
    }

    public Environment loadConfigurationFile() throws BluePrinterException {
        return FileUtils.loadConfigurationFile(this.getConfigurationFile(), Environment.class, FILE_LOCATION + SCHEMA_FILE);
    }

    public String getJarDirectoryLocation() {
        return home.getDir().toString();
    }

    public String getHomeDirectoryPath() {
        return Optional.ofNullable(getHomeDirectory()).map(File::getAbsolutePath).orElse(null);
    }

    public boolean checkHomeDirectory() {
        log.info("Check home directory, home directory is {}", getHomeDirectoryPath());
        return Optional.ofNullable(this.getHomeDirectory()).filter(File::exists).filter(File::isDirectory).isPresent();
    }

    /**
     * Check if the environment is initialized.
     * <p>The environment is initialized if :</p>
     * <ul>
     *     <li>The home directory is set</li>
     *     <li>The workspace directory exists</li>
     *     <li>The model directory exists</li>
     *     <li>The template directory exists</li>
     *     <li>The configuration file exists</li>
     *     <li>The schema file exists</li>
     * </ul>
     * @return {@code true} if the environment is initialized, {@code false} otherwise
     */
    public boolean checkEnvironmentInitialized() {
        log.debug("Check environment initialized");
        return checkHomeDirectory()
                && checkWorkspaceDirectory()
                && checkModelDirectory()
                && checkTemplateDirectory()
                && checkConfigurationFile()
                && checkSchemaFile();
    }
}
