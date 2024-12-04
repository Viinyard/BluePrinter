package dev.vinyard.blueprinter.core.model;

import dev.vinyard.blueprinter.core.environment.EnvironmentManager;
import dev.vinyard.blueprinter.core.environment.entities.Environment;
import dev.vinyard.blueprinter.core.exception.BluePrinterException;
import dev.vinyard.blueprinter.core.model.entities.Directive;
import dev.vinyard.blueprinter.core.model.entities.Directives;
import dev.vinyard.blueprinter.core.model.entities.Model;
import dev.vinyard.blueprinter.core.template.TemplateManager;
import dev.vinyard.blueprinter.core.utils.DirectoryUtils;
import dev.vinyard.blueprinter.core.utils.FileUtils;
import dev.vinyard.blueprinter.core.utils.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Manager for the models.
 * <p>Handle the creation, deletion, listing and loading of the models.</p>
 */
@Slf4j
public class ModelManager {

    public static final String FILE_LOCATION = "config/model/";
    public static final String CONFIGURATION_FILE = "model.xml";
    public static final String SCHEMA_FILE = "model.xsd";

    private final EnvironmentManager environmentManager;

    private final TemplateManager templateManager;

    public ModelManager(EnvironmentManager environmentManager, TemplateManager templateManager) {
        this.environmentManager = environmentManager;
        this.templateManager = templateManager;
    }

    /**
     * Create a new model with the given name.
     * <p>Create a new folder in the model directory with the given name.</p>
     * <p>Copy the model configuration file and the model configuration validator file in the new folder.</p>
     *
     * @param name The name of the model to create
     */
    public void createModel(String name) {
        File modelFolder = DirectoryUtils.createFolder(environmentManager.getModelDirectory(), name);
        log.info("Model directory {} created.", modelFolder.getAbsolutePath());

        File xmlFile = FileUtils.copyFile(FILE_LOCATION + CONFIGURATION_FILE, new File(modelFolder, CONFIGURATION_FILE));
        log.info("Model configuration file {} created.", xmlFile.getAbsolutePath());

        File xsdFile = FileUtils.copyFile(FILE_LOCATION + SCHEMA_FILE, new File(modelFolder, SCHEMA_FILE));
        log.info("Model configuration validator file {} created.", xsdFile.getAbsolutePath());
    }

    /**
     * Delete the model with the given name.
     * <p>Delete the folder of the model with the given name.</p>
     * @param name The name of the model to delete
     */
    public void deleteModel(String name) {
        File modelFolder = new File(environmentManager.getModelDirectory(), name);

        if (!modelFolder.exists())
            throw new RuntimeException(String.format("Model %s does not exist.", name));

        DirectoryUtils.deleteFolder(modelFolder);

        log.info("Model {} deleted.", name);
    }

    /**
     * List all models.
     * @return formatted string with all models names
     */
    public String listModels() {
        StringBuilder stringBuilder = new StringBuilder();
        Consumer<String> appendLine = (value) -> stringBuilder.append(value).append(environmentManager.getLineSeparator());

        List<String> models = findAllModels();

        if (models == null || models.isEmpty()) {
            appendLine.accept("No model found.");
            return stringBuilder.toString();
        }

        appendLine.accept("Models:");
        models.forEach((model) -> appendLine.accept(String.format(" - %s", model)));

        return stringBuilder.toString();
    }

    public File getModelFolder(String name) {
        return new File(environmentManager.getModelDirectory(), name);
    }

    public File getConfigurationFile(String name) {
        return new File(getModelFolder(name), CONFIGURATION_FILE);
    }

    public File getSchemaFile(String name) {
        return new File(getModelFolder(name), SCHEMA_FILE);
    }

    /**
     * Check if the model with the given name is valid.
     * <p>A model is valid if :</p>
     * <ul>
     *     <li>The model folder exists</li>
     *     <li>The model configuration file exists</li>
     *     <li>The model configuration validator file exists</li>
     *     <li>The model configuration file is valid</li>
     * </ul>
     * @param name The name of the model to check
     * @throws BluePrinterException if the model is not valid
     */
    public void checkModel(String name) throws BluePrinterException {
        File modelFolder = getModelFolder(name);

        if (!modelFolder.exists()) {
            throw new BluePrinterException(String.format("Model %s does not exist.", name));
        }

        File xmlFile = getConfigurationFile(name);
        if (!xmlFile.exists()) {
            throw new BluePrinterException(String.format("Model configuration file %s does not exist.", xmlFile.getName()));
        }

        File xsdFile = getSchemaFile(name);
        if (!xsdFile.exists()) {
            throw new BluePrinterException(String.format("Model configuration validator file %s does not exist.", xsdFile.getName()));
        }

        this.checkConfigurationFile(xmlFile);

        log.info("Model {} is valid.", name);
    }

    /**
     * Check if the model configuration file is valid.
     * <p>Loading the model configuration file and check if all the templates of the directives are valid.</p>
     * @param xmlFile The model configuration file to check
     * @throws BluePrinterException if the model configuration file is not valid
     */
    private void checkConfigurationFile(File xmlFile) throws BluePrinterException {
        Model model = loadConfigurationFile(xmlFile);

        List<String> templates = Optional.of(model).map(Model::getDirectives).map(Directives::getDirectiveList).orElseGet(Collections::emptyList).stream().map(Directive::getTemplate).toList();

        templates.forEach(templateManager::checkTemplate);
    }

    /**
     * Load the model with the given name.
     * <p>Lod the model configuration file and add the properties of the model to the velocity context.</p>
     *
     * @param velocityContext The velocity context to use. The context is a map of key value pairs that can be used in the templates.
     * @param name The name of the model to load
     * @return The model loaded
     * @throws BluePrinterException if an error occurs during the loading
     * @throws IOException if an error occurs during file reading
     */
    public Model loadModel(VelocityContext velocityContext, String name) throws BluePrinterException, IOException {
        File xmlFile = getConfigurationFile(name);
        Environment environment = this.environmentManager.loadConfigurationFile();

        log.info("Put environment properties on velocity context");

        environment.getProperties().getPropertyList().forEach(property -> {
            String value = VelocityUtils.processTemplate(velocityContext, property.getValue());
            log.info("Put property on velocity context {} : {}", property.getKey(), value);
            velocityContext.put(property.getKey(), value);
        });

        return loadConfigurationFile(xmlFile);
    }

    private Model loadConfigurationFile(File xmlFile) throws BluePrinterException {
        return FileUtils.loadConfigurationFile(xmlFile, Model.class, FILE_LOCATION + SCHEMA_FILE);
    }

    private Model loadConfigurationFile(String xml) throws BluePrinterException {
        return FileUtils.loadConfigurationFile(xml, Model.class, FILE_LOCATION + SCHEMA_FILE);
    }

    /**
     * List all models.
     * @return List of all models names
     */
    public List<String> findAllModels() {
        return DirectoryUtils.listFolders(environmentManager.getModelDirectory()).stream().map(File::getName).toList();
    }
}
