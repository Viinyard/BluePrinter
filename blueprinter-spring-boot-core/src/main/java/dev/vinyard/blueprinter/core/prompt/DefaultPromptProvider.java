package dev.vinyard.blueprinter.core.prompt;

import dev.vinyard.blueprinter.core.model.entities.*;
import dev.vinyard.blueprinter.core.utils.VelocityUtils;
import org.apache.velocity.VelocityContext;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class DefaultPromptProvider implements PromptProvider {

    private final PromptRepository promptRepository;

    public DefaultPromptProvider(PromptRepository promptRepository) {
        this.promptRepository = promptRepository;
    }

    @Override
    public Object prompt(PromptType promptType, VelocityContext velocityContext) {
        Function<String, String> templated = s -> VelocityUtils.processTemplate(velocityContext, s);
        return switch (promptType) {
            case MultiSelect e ->
                    this.multiSelector(e.getValueList().stream().map(v -> PromptSelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()), v.isEnabled(), v.isSelected())).toList(), e.getValue());
            case MonoSelect e ->
                    this.singleSelect(e.getValueList().stream().map(v -> PromptSelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()), v.isEnabled(), v.isSelected())).toList(), e.getValue());
            case StringInput e ->
                    this.stringInput(templated.apply(e.getValue()), Optional.ofNullable(e.getDefaultValue()).map(DefaultValue::getContent).map(templated).orElse(null), false);
            case SetupInput e -> Optional.ofNullable(e.getSetup()).map(Setup::getContent).map(templated).orElse("");
            default -> throw new IllegalStateException("Unexpected value: " + promptType);
        };
    }

    @Override
    public <T> List<T> multiSelector(List<PromptSelectorItem<T>> items, String value) {
        return this.promptRepository.findById(value)
                .map(Prompt::getSelectedValues)
                .map(values -> values.stream().flatMap(v -> this.findItems(items, v)))
                .orElseGet(() -> findDefaultItems(items))
                .toList();
    }

    private <T> Stream<T> findItems(List<PromptSelectorItem<T>> items, String value) {
        return items.stream().filter(PromptSelectorItem::enabled).filter(i -> Objects.equals(value, i.name())).map(PromptSelectorItem::item);
    }

    private <T> Stream<T> findDefaultItems(List<PromptSelectorItem<T>> items) {
        return items.stream().filter(PromptSelectorItem::enabled).filter(PromptSelectorItem::selected).map(PromptSelectorItem::item);
    }

    @Override
    public <T> T singleSelect(List<PromptSelectorItem<T>> items, String value) {
        return this.promptRepository.findById(value)
                .map(Prompt::getSelectedValues)
                .map(values -> values.stream().flatMap(v -> this.findItems(items, v)))
                .orElseGet(() -> findDefaultItems(items))
                .findAny().orElseThrow(() -> new IllegalArgumentException("No item selected for " + value));
    }

    @Override
    public String stringInput(String value, String defaultValue, boolean mask) {
        return this.promptRepository.findById(value)
                .map(Prompt::getSelectedValues)
                .map(Collection::stream)
                .orElseGet(() -> Stream.of(defaultValue))
                .findAny().orElseThrow(() -> new IllegalArgumentException("No item selected for " + value));
    }
}
