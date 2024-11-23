package dev.vinyard.bp.core.prompt;

public record PromptSelectorItem<T>(String name, T item, boolean enabled, boolean selected) {

    public static <T> PromptSelectorItem<T> of(String name, T item) {
        return of(name, item, true, false);
    }

    public static <T> PromptSelectorItem<T> of(String name, T item, boolean enabled, boolean selected) {
        return new PromptSelectorItem<>(name, item, enabled, selected);
    }
}
