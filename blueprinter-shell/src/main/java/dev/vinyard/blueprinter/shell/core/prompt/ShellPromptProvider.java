package dev.vinyard.blueprinter.shell.core.prompt;

import dev.vinyard.blueprinter.core.model.entities.*;
import dev.vinyard.blueprinter.core.prompt.PromptProvider;
import dev.vinyard.blueprinter.core.prompt.PromptSelectorItem;
import dev.vinyard.blueprinter.core.utils.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ShellPromptProvider extends AbstractShellComponent implements PromptProvider {

    private static <T> SelectorItem<T> toSelectorItem(PromptSelectorItem<T> promptSelectorItem) {
        return SelectorItem.of(promptSelectorItem.name(), promptSelectorItem.item(), promptSelectorItem.enabled(), promptSelectorItem.selected());
    }

    private static <T> List<SelectorItem<T>> toSelectorItems(List<PromptSelectorItem<T>> promptSelectorItems) {
        return promptSelectorItems.stream().map(ShellPromptProvider::toSelectorItem).collect(Collectors.toList());
    }

    @Override
    public Object prompt(PromptType promptType, VelocityContext velocityContext) {
        Function<String, String> templated = s -> VelocityUtils.processTemplate(velocityContext, s);
        return switch (promptType) {
            case MultiSelect e ->
                    this.multiSelector(e.getValueList().stream().map(v -> PromptSelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()), v.isEnabled(), v.isSelected())).toList(), e.getValue());
            case MonoSelect e ->
                    this.singleSelect(e.getValueList().stream().map(v -> PromptSelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()))).toList(), e.getValue());
            case StringInput e ->
                    this.stringInput(templated.apply(e.getValue()), Optional.ofNullable(e.getDefaultValue()).map(DefaultValue::getContent).map(templated).orElse(""), e.isMasked());
            case SetupInput e -> Optional.ofNullable(e.getSetup()).map(Setup::getContent).map(templated).orElse("");
            default -> throw new IllegalStateException("Unexpected value: " + promptType);
        };
    }

    /**
     * Multi selector
     * <p>Ask the user to select multiple items from a list</p>
     *
     * @param promptSelectorItems list of items to select
     * @param value message to display
     * @return list of selected items
     * @param <T> type of the items
     */
    @Override
    public <T> List<T> multiSelector(List<PromptSelectorItem<T>> promptSelectorItems, String value) {
        MultiItemSelector<T, SelectorItem<T>> component = new MultiItemSelector<>(getTerminal(), toSelectorItems(promptSelectorItems), value, null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        MultiItemSelector.MultiItemSelectorContext<T, SelectorItem<T>> context = component.run(MultiItemSelector.MultiItemSelectorContext.empty());

        return context.getResultItems().stream().map(Itemable::getItem).toList();
    }

    /**
     * Single selector
     * <p>Ask the user to select a single item from a list</p>
     *
     * @param promptSelectorItems list of items to select
     * @param value message to display
     * @return the selected item
     * @param <T> type of the items
     */
    @Override
    public <T> T singleSelect(List<PromptSelectorItem<T>> promptSelectorItems, String value) {
        SingleItemSelector<T, SelectorItem<T>> component = new SingleItemSelector<>(getTerminal(), toSelectorItems(promptSelectorItems), value, null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<T, SelectorItem<T>> context = component.run(SingleItemSelector.SingleItemSelectorContext.empty());

        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElseThrow();
    }

    /**
     * String input
     * <p>Ask the user to input a string</p>
     *
     * @param value message to display
     * @param defaultValue default value if the user does not input anything
     * @param mask true if the input should be masked
     * @return the input string
     */
    @Override
    public String stringInput(String value, String defaultValue, boolean mask) {
        org.springframework.shell.component.StringInput component = new org.springframework.shell.component.StringInput(getTerminal(), value, defaultValue);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        if (mask) {
            component.setMaskCharacter('*');
        }
        org.springframework.shell.component.StringInput.StringInputContext context = component.run(org.springframework.shell.component.StringInput.StringInputContext.empty());
        return context.getResultValue();
    }
}
