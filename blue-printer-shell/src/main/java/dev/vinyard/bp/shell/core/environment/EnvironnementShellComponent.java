package dev.vinyard.bp.shell.core.environment;

import dev.vinyard.bp.engine.core.environment.EnvironmentManager;
import dev.vinyard.bp.engine.core.environment.EnvironmentProperties;
import dev.vinyard.bp.shell.custom.DirectoryInput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.component.PathInput;
import org.springframework.shell.component.PathSearch;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Shell component to manage the environment
 * <p>It allows to display the environment properties and to initialize the environment.</p>
 */
@ShellComponent
public class EnvironnementShellComponent extends AbstractShellComponent {

    private static final String PROPERTY_SEPARATOR = " : ";

    private final EnvironmentManager environmentManager;

    public EnvironnementShellComponent(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    /**
     * Display all environnement properties in a formatted way :
     * <ul>
     *     <li>User environnement</li>
     *     <ul>
     *         <li>Home directory</li>
     *         <li>Working directory</li>
     *     </ul>
     *     <li>Java environnement</li>
     *     <ul>
     *         <li>Java version</li>
     *         <li>Jar File</li>
     *         <li>Jar directory</li>
     *     </ul>
     *     <li>System environnement</li>
     *     <ul>
     *         <li>Operating system</li>
     *     </ul>
     * </ul>
     * @return formatted string
     */
    @ShellMethod(key = "environnement", value = "Display environnement", group = "EnvironnementManager")
    public String displayEnvironnement() {
        StringBuilder stringBuilder = new StringBuilder();
        BiConsumer<String, String> appendLine = (key, value) -> appendLine(stringBuilder, key, value);
        Consumer<String> appendCategory = (value) -> appendCategory(stringBuilder, value);
        appendCategory.accept("User environnement");
        appendLine.accept("Home directory", environmentManager.getHomeDirectoryPath());
        appendLine.accept("Working directory", environmentManager.getWorkingDirectory());
        appendCategory.accept("Java environnement");
        appendLine.accept("Java version", environmentManager.getJavaVersion());
        appendLine.accept("Jar File", environmentManager.getJarFileLocation());
        appendLine.accept("Jar directory", environmentManager.getJarDirectoryLocation());
        appendCategory.accept("System environnement");
        appendLine.accept("Operating system", environmentManager.getOperatingSystem());
        appendCategory.accept("");
        return stringBuilder.toString();
    }

    /**
     * Prompt user to set home directory
     * <p>The user is asked to select a valid directory to select or replace his home directory.</p>
     * <p>Home directory is the directory where your workspace is generated.</p>
     * @see EnvironnementShellComponent#init() to initialize the environment after setting the home directory
     */
    @ShellMethod(key = "home", value = "Set home directory", group = "EnvironnementManager")
    public void setHomeDirectory() {
        DirectoryInput component = new DirectoryInput(getTerminal(), "Enter home directory");
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        PathInput.PathInputContext context = component.run(PathSearch.PathSearchContext.empty());

        environmentManager.setHomeDirectory(context.getResultValue());
    }

    /**
     * Initialize the environment
     * <p>The workspace is initialized in the home directory by :</p>
     * <ul>
     *      <li>Creating the workspace directory, named {@link EnvironmentProperties.Environment#getWorkspaceDirectory()}</li>
     *      <li>Creating the model directory in the workspace, named {@link EnvironmentProperties.Environment#getModelDirectory()}</li>
     *      <li>Creating the template directory in the workspace, named {@link EnvironmentProperties.Environment#getTemplateDirectory()}</li>
     *      <li>Creating the {@code environment.xml} file and his validator {@code environment.xsd} file in the workspace.</li>
     *      <li>Creating the plugin directory in the workspace, named {@code plugins}</li>
     * </ul>
     */
    @ShellMethod(key = "init", value = "Initialize environnement", group = "EnvironnementManager")
    @ShellMethodAvailability("environmentAvailability")
    public void init() {
        environmentManager.initialize();
    }

    /**
     * Check if the home directory is set to get the availability of the {@code init} command
     * @return {@link Availability#available()} if the home directory is set, {@link Availability#unavailable(String)} otherwise
     */
    public Availability environmentAvailability() {
        return environmentManager.checkHomeDirectory() ? Availability.available() : Availability.unavailable("Home directory is not set");
    }

    private void appendCategory(StringBuilder stringBuilder, String category) {
        stringBuilder.append(StringUtils.center(category, 100 + PROPERTY_SEPARATOR.length(), "-"));
        stringBuilder.append(environmentManager.getLineSeparator());
    }

    private void appendLine(StringBuilder stringBuilder, String key, String value) {
        stringBuilder.append(String.format("%1$-20s%2$s%3$80s", key, PROPERTY_SEPARATOR, Optional.ofNullable(value).orElse("(undefined)")));
        stringBuilder.append(environmentManager.getLineSeparator());
    }
}
