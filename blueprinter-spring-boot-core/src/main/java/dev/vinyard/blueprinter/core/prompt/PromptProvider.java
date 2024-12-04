package dev.vinyard.blueprinter.core.prompt;

import dev.vinyard.blueprinter.core.model.entities.PromptType;
import org.apache.velocity.VelocityContext;

import java.util.List;

public interface PromptProvider {

    Object prompt(PromptType promptType, VelocityContext velocityContext);

    <T> List<T> multiSelector(List<PromptSelectorItem<T>> items, String value);

    <T> T singleSelect(List<PromptSelectorItem<T>> items, String value);

    String stringInput(String value, String defaultValue, boolean mask);
}
