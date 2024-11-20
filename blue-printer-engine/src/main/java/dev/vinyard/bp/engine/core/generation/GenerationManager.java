package dev.vinyard.bp.engine.core.generation;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;
import org.pf4j.PluginManager;
import dev.vinyard.bp.engine.core.environment.EnvironmentManager;
import dev.vinyard.bp.engine.core.exception.BluePrinterException;
import dev.vinyard.bp.engine.core.model.ModelManager;
import dev.vinyard.bp.engine.core.model.entities.*;
import dev.vinyard.bp.engine.core.pluginManager.BpPluginManager;
import dev.vinyard.bp.engine.core.utils.VelocityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Manager for the generation.
 * <p>Handle the generation of the models.</p>
 */
@Slf4j
public class GenerationManager {

    private final ModelManager modelManager;

    private final BpPluginManager bpPluginManager;

    private final EnvironmentManager environmentManager;

    public GenerationManager(ModelManager modelManager, BpPluginManager bpPluginManager, EnvironmentManager environmentManager) {
        this.modelManager = modelManager;
        this.bpPluginManager = bpPluginManager;
        this.environmentManager = environmentManager;
    }

    /**
     * Generate the model with the given name.
     * <p>Create a new velocity context and put the model properties and prompts on it.</p>
     * <p>Add date and math tools to the context.</p>
     * <p>Initialize the plugins. Plugins are other costum tools for velocity that can be used in the templates.</p>
     * <p>Then, process the directives of the model.</p>
     *
     * @param modelName The name of the model to generate from
     * @param promptProcessor The function to process the prompts, a prompt is a question asked to the user to fill a value in the model
     * @throws BluePrinterException if an error occurs during generation
     * @throws IOException if an error occurs during file reading or writing
     */
    public void generate(String modelName, BiFunction<PromptType, VelocityContext, Object> promptProcessor) throws BluePrinterException, IOException {
        this.modelManager.checkModel(modelName);

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("date", new DateTool());
        velocityContext.put("math", new MathTool());

        PluginManager pluginManager = bpPluginManager.initPlugins(velocityContext);

        Model model = this.modelManager.loadModel(velocityContext, modelName);

        Optional.of(model).map(Model::getProperties).map(Properties::getPropertyList).orElseGet(Collections::emptyList).stream()
                .peek(p -> log.debug("Put property on velocity context {} : {}", p.getKey(), p.getValue()))
                .forEach(p -> velocityContext.put(p.getKey(), p.getValue()));

        Stream.of(
                Optional.of(model).map(Model::getPrompts).map(Prompts::getMultiSelectList).orElseGet(Collections::emptyList),
                Optional.of(model).map(Model::getPrompts).map(Prompts::getMonoSelectList).orElseGet(Collections::emptyList),
                Optional.of(model).map(Model::getPrompts).map(Prompts::getStringInputList).orElseGet(Collections::emptyList)
        ).flatMap(List::stream).map(PromptType.class::cast).sorted().forEach(s -> {
            Object value = promptProcessor.apply(s, velocityContext);
            log.info("Put prompt on velocity context {} : {}", s.getValue(), value);
            velocityContext.put(s.getValue(), value);
        });

        model.getDirectives().getDirectiveList().forEach(d -> {
            try {
                this.processDirective(velocityContext, d);
            } catch (Exception e) {
                throw new RuntimeException("Cannot process template.", e);
            }
        });

        bpPluginManager.unloadPlugins(pluginManager);
    }

    /**
     * Process the directive with the given velocity context.
     * <p>Process the template of the directive and write the result in the file of the directive.</p>
     *
     * @param velocityContext The velocity context to use. The context is a map of key value pairs that can be used in the templates.
     * @param directive The directive to process
     */
    public void processDirective(VelocityContext velocityContext, Directive directive) {
        Function<String, String> templated = s -> VelocityUtils.processTemplate(velocityContext, s);
        File template = new File(environmentManager.getTemplateDirectory(), Optional.of(directive).map(Directive::getTemplate).map(templated).map(String::trim).orElse(""));
        File file = new File(environmentManager.getHomeDirectory(), Optional.of(directive).map(Directive::getValue).map(templated).map(String::trim).orElse(""));
        VelocityUtils.processTemplate(velocityContext, template, file);
    }



}
