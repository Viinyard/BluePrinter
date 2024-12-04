package dev.vinyard.blueprinter.shell.core.generation;

import dev.vinyard.blueprinter.core.exception.BluePrinterException;
import dev.vinyard.blueprinter.core.generation.GenerationManager;
import dev.vinyard.blueprinter.core.model.ModelManager;
import dev.vinyard.blueprinter.core.prompt.PromptProvider;
import dev.vinyard.blueprinter.core.prompt.PromptSelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.util.List;

/**
 * <p>Shell component for generation commands.</p>
 * <p>It allows to generate files from a model.</p>
 */
@ShellComponent
public class GenerationShellComponent extends AbstractShellComponent {

    private final ModelManager modelManager;

    private final GenerationManager generationManager;

    private final PromptProvider promptProvider;

    public GenerationShellComponent(ModelManager modelManager, GenerationManager generationManager, PromptProvider promptProvider) {
        this.modelManager = modelManager;
        this.generationManager = generationManager;
        this.promptProvider = promptProvider;
    }

    /**
     * <p>Génère un ou plusieurs fichiers à partir d'un modèle.</p>
     *
     * <ul>
     *     <li>L'utilisateur est invité à sélectionné un model à partir de son nom parmi la liste de tous les models disponibles.</li>
     *     <li>Le modèle est chargé et les propriétés du modèle sont ajoutées au contexte Velocity.</li>
     *     <li>Les prompts du modèle sont affichés à l'utilisateur.</li>
     *     <li>Les valeurs des prompts sont récupérées en fonction du type de prompt et ajoutées au context Velocity.</li>
     *     <li>Chaque directive du model est exécutée et génère un fichier à partir du template en fonction du context Velocity.</li>
     *     <li>Les fichiers générés sont enregistrés dans le fichier de sortie de la directive.</li>
     * </ul>
     *
     * @throws BluePrinterException si une erreur survient lors de la génération.
     * @throws IOException si une erreur survient lors de la lecture ou de l'écriture d'un fichier.
     */
    @ShellMethod(key = "generate", value = "Generate from a model", group = "Generation")
    public void generate() throws BluePrinterException, IOException {
        List<PromptSelectorItem<String>> selectorItemList = this.modelManager.findAllModels().stream().map(m -> PromptSelectorItem.of(m, m)).toList();
        String modelName = this.promptProvider.singleSelect(selectorItemList, "Select models to generate from");

        generationManager.generate(modelName);
    }
}
