package pro.vinyard.vb.shell.shell;

import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.List;
import java.util.Optional;

/**
 * Utility class to implement custom shell components.
 * <p>It provides methods to ask the user to select, input or choose items</p>
 */
public class CustomAbstractShellComponent extends AbstractShellComponent {

    /**
     * Multi selector
     * <p>Ask the user to select multiple items from a list</p>
     *
     * @param items list of items to select
     * @param message message to display
     * @return list of selected items
     * @param <T> type of the items
     */
    public <T> List<T> multiSelector(List<SelectorItem<T>> items, String message) {
        MultiItemSelector<T, SelectorItem<T>> component = new MultiItemSelector<>(getTerminal(), items, message, null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        MultiItemSelector.MultiItemSelectorContext<T, SelectorItem<T>> context = component.run(MultiItemSelector.MultiItemSelectorContext.empty());

        return context.getResultItems().stream().map(Itemable::getItem).toList();
    }

    /**
     * String input
     * <p>Ask the user to input a string</p>
     *
     * @param message message to display
     * @param defaultValue default value if the user does not input anything
     * @param mask true if the input should be masked
     * @return the input string
     */
    public String stringInput(String message, String defaultValue, boolean mask) {
        StringInput component = new StringInput(getTerminal(), message, defaultValue);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        if (mask) {
            component.setMaskCharacter('*');
        }
        StringInput.StringInputContext context = component.run(StringInput.StringInputContext.empty());
        return context.getResultValue();
    }

    /**
     * Single selector
     * <p>Ask the user to select a single item from a list</p>
     *
     * @param items list of items to select
     * @param message message to display
     * @return the selected item
     * @param <T> type of the items
     */
    public <T> T singleSelect(List<SelectorItem<T>> items, String message) {
        SingleItemSelector<T, SelectorItem<T>> component = new SingleItemSelector<>(getTerminal(), items, message, null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<T, SelectorItem<T>> context = component.run(SingleItemSelector.SingleItemSelectorContext.empty());

        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElseThrow();
    }


}
