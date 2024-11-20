package dev.vinyard.bp.engine.core.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import dev.vinyard.bp.engine.core.environment.EnvironmentManager;
import dev.vinyard.bp.engine.core.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class TemplateManager {

    public static final String FILE_LOCATION = "config/template/";
    public static final String TEMPLATE_FILE = "template.vm";

    private final EnvironmentManager environmentManager;

    public TemplateManager(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    /**
     * Find all templates in the template directory.
     *
     * @return List of templates
     */
    public List<File> findAllTemplates() {
        return FileUtils.listFiles(environmentManager.getTemplateDirectory(), "vm");
    }

    /**
     * Check if the template exists.
     * @param name The name of the template
     */
    public void checkTemplate(String name) {
        File templateFile = new File(environmentManager.getTemplateDirectory(), name);

        if (!templateFile.exists())
            throw new IllegalArgumentException(String.format("Template %s does not exist.", name));
    }

    /**
     * List all templates in the template directory.
     * @return formatted string with all templates
     */
    public String listTemplates() {
        StringBuilder stringBuilder = new StringBuilder();
        Consumer<String> appendLine = (value) -> stringBuilder.append(value).append(environmentManager.getLineSeparator());

        List<String> templates = findAllTemplates().stream().map(File::getAbsolutePath).map(s -> StringUtils.removeStart(s, environmentManager.getTemplateDirectory().getAbsolutePath())).map(s -> StringUtils.removeStart(s, "\\")).toList();

        if (templates.isEmpty()) {
            appendLine.accept("No template found.");
            return stringBuilder.toString();
        }

        appendLine.accept("Templates:");
        templates.forEach((t) -> appendLine.accept(String.format(" - %s", t)));

        return stringBuilder.toString();
    }

    /**
     * Create a new template with the given name.
     * <p>Copy the template file in the template directory with the given name.</p>
     * @param name The name of the template to create
     */
    public void createTemplate(String name) {
        FileUtils.copyFile(FILE_LOCATION + TEMPLATE_FILE, new File(environmentManager.getTemplateDirectory(), name + ".vm"));
        log.info("Template {} created.", name + ".vm");
    }

    /**
     * Delete the template with the given name.
     * @param name The name of the template to delete
     */
    public void deleteTemplate(String name) {
        File templateFile = new File(environmentManager.getTemplateDirectory(), name);

        if (!templateFile.exists())
            throw new RuntimeException(String.format("Template %s does not exist.", name));

        if (templateFile.delete())
            log.info("Template {} deleted.", name);
        else
            log.warn("Template {} not deleted.", name);
    }
}
