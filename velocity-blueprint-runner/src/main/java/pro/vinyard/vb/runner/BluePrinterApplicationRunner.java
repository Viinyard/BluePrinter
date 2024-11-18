package pro.vinyard.vb.runner;

import org.apache.velocity.VelocityContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.engine.core.exception.VelocityBlueprintException;
import pro.vinyard.vb.engine.core.generation.GenerationManager;
import pro.vinyard.vb.engine.core.model.entities.*;
import pro.vinyard.vb.engine.core.utils.VelocityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class BluePrinterApplicationRunner implements ApplicationRunner {

    private final GenerationManager generationManager;

    private final EnvironmentManager environmentManager;

    private static final String GENERATE = "generate";
    private static final String HOME_DIR = "home";

    public BluePrinterApplicationRunner(GenerationManager generationManager, EnvironmentManager environmentManager) {
        this.generationManager = generationManager;
        this.environmentManager = environmentManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (args.containsOption(HOME_DIR)) {
            this.setHomeDirectory(args);
        }

        if (args.containsOption(GENERATE)) {
            this.generate(args);
        }
    }

    private void generate(ApplicationArguments arguments) throws VelocityBlueprintException, IOException {
        if (!this.environmentManager.checkEnvironmentInitialized())
            throw new IllegalStateException("Environment not initialized");

        List<String> values = arguments.getOptionValues(GENERATE);

        for (String model : values) {
            generationManager.generate(model, (promptType, velocityContext) -> this.prompt(promptType, velocityContext, arguments));
        }
    }

    private void setHomeDirectory(ApplicationArguments arguments) {
        List<String> homeDirArgs = arguments.getOptionValues(HOME_DIR);

        if (homeDirArgs.size() > 1)
            throw new IllegalArgumentException("Only one value is allowed for " + HOME_DIR);

        Path path = homeDirArgs.stream().findFirst().map(Path::of).orElseThrow(() -> new IllegalArgumentException("No value for " + HOME_DIR));

        boolean isDirectory = Files.isDirectory(path);
        if (!isDirectory)
            throw new IllegalArgumentException("The path provided is not a directory");

        this.environmentManager.setHomeDirectory(path);
    }

    public Object prompt(PromptType promptType, VelocityContext velocityContext, ApplicationArguments args) {
        Function<String, String> templated = s -> VelocityUtils.processTemplate(velocityContext, s);
        return switch (promptType) {
            case MultiSelect e -> this.multiSelector(e.getValueList().stream().map(v -> SelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()), v.isEnabled(), v.isSelected())).toList(), e.getValue(), args);
            case MonoSelect e -> this.singleSelect(e.getValueList().stream().map(v -> SelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()))).toList(), e.getValue(), args);
            case StringInput e -> this.stringInput(templated.apply(e.getValue()), Optional.ofNullable(e.getDefaultValue()).map(DefaultValue::getContent).map(templated).orElse(""), args);
            default -> throw new IllegalStateException("Unexpected value: " + promptType);
        };
    }

    private <T> List<T> multiSelector(List<SelectorItem<T>> items, String value, ApplicationArguments args) {
        if (args.containsOption(value)) {
            List<String> values = args.getOptionValues(value);
            return items.stream().filter(SelectorItem::enabled).filter(v -> values.contains(v.name())).map(SelectorItem::item).toList();
        } else {
            return items.stream().filter(SelectorItem::enabled).filter(SelectorItem::selected).map(SelectorItem::item).toList();
        }
    }

    private <T> T singleSelect(List<SelectorItem<T>> items, String value, ApplicationArguments args) {
        if (args.containsOption(value)) {
            List<String> values = args.getOptionValues(value);
            if (values.size() > 1)
                throw new IllegalArgumentException("Only one value is allowed for " + value);

            String selectedValue = Optional.of(values).map(List::getFirst).orElseThrow(() -> new IllegalArgumentException("No value selected for " + value));

            return items.stream().filter(SelectorItem::enabled).filter(v -> Objects.equals(selectedValue, v.name())).findFirst().map(SelectorItem::item).orElseThrow(() -> new IllegalArgumentException(String.format("No item found for value %s with key %s", value, selectedValue)));
        } else {
            return items.stream().filter(SelectorItem::enabled).filter(SelectorItem::selected).findFirst().map(SelectorItem::item).orElseThrow(() -> new IllegalArgumentException("No item selected for " + value));
        }
    }

    private String stringInput(String value, String defaultValue, ApplicationArguments args) {
        if (args.containsOption(value)) {
            List<String> values = args.getOptionValues(value);
            if (values.size() > 1)
                throw new IllegalArgumentException("Only one value is allowed for " + value);

            return Optional.of(values).map(List::getFirst).orElseThrow(() -> new IllegalArgumentException("No value found for " + value));
        } else {
            return defaultValue;
        }
    }
}
