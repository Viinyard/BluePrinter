package pro.vinyard.vb.shell.core.generation;

import org.apache.velocity.VelocityContext;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import pro.vinyard.vb.engine.core.exception.VelocityBlueprintException;
import pro.vinyard.vb.engine.core.generation.GenerationManager;
import pro.vinyard.vb.engine.core.model.ModelManager;
import pro.vinyard.vb.engine.core.model.entities.*;
import pro.vinyard.vb.engine.core.utils.VelocityUtils;
import pro.vinyard.vb.shell.shell.CustomAbstractShellComponent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * <p>Shell component for generation commands.</p>
 * <p>It allows to generate files from a model.</p>
 */
@ShellComponent
public class GenerationShellComponent extends CustomAbstractShellComponent {

    private final ModelManager modelManager;

    private final GenerationManager generationManager;

    public GenerationShellComponent(ModelManager modelManager, GenerationManager generationManager) {
        this.modelManager = modelManager;
        this.generationManager = generationManager;
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
     * @throws VelocityBlueprintException si une erreur survient lors de la génération.
     * @throws IOException si une erreur survient lors de la lecture ou de l'écriture d'un fichier.
     */
    @ShellMethod(key = "generate", value = "Generate from a model", group = "Generation")
    public void generate() throws VelocityBlueprintException, IOException {
        List<SelectorItem<String>> selectorItemList = this.modelManager.findAllModels().stream().map(m -> SelectorItem.of(m, m)).toList();
        String modelName = this.singleSelect(selectorItemList, "Select models to generate from");

        generationManager.generate(modelName, this::prompt);
    }

    /**
     * <p>Affiche un prompt à l'utilisateur en fonction du type de prompt.</p>
     * <p>Un prompt peut être de type :</p>
     * <ul>
     *   <li><code>MonoSelect</code> qui est un prompt qui permet de sélectionner un seul élément parmi une liste d'éléments.</li>
     *   <li><code>MultiSelect</code> qui est un prompt qui permet de sélectionner plusieurs éléments parmi une liste d'éléments.</li>
     *   <li><code>StringInput</code> qui est un prompt qui permet de saisir une chaîne de caractères.</li>
     * </ul>
     * <p>Les éléments d'un prompt peuvent être des templates Velocity qui seront complétés avec le contexte Velocity fourni.</p>
     * <p>Le context Velocity est complété avec les propriétés du modèle, mais aussi avec les valeurs des prompts précédents associés à leur clé.</p>
     *
     * @param promptType le type de prompt (MonoSelect, MultiSelect, StringInput)
     * @param velocityContext le contexte Velocity utilisé pour compléter les templates. La valeur d'un prompt peut être un template.
     * @return La valeur saisie ou sélectionnée par l'utilisateur.
     */
    public Object prompt(PromptType promptType, VelocityContext velocityContext) {
        Function<String, String> templated = s -> VelocityUtils.processTemplate(velocityContext, s);
        return switch (promptType) {
            case MultiSelect e ->
                    this.multiSelector(e.getValueList().stream().map(v -> SelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()), v.isEnabled(), v.isSelected())).toList(), e.getValue());
            case MonoSelect e ->
                    this.singleSelect(e.getValueList().stream().map(v -> SelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()), v.isEnabled(), v.isSelected())).toList(), e.getValue());
            case StringInput e ->
                    this.stringInput(templated.apply(e.getValue()), Optional.ofNullable(e.getDefaultValue()).map(DefaultValue::getContent).map(templated).orElse(""), e.isMasked());
            default -> throw new IllegalStateException("Unexpected value: " + promptType);
        };
    }

}
