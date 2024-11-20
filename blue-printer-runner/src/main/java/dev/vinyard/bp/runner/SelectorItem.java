package dev.vinyard.bp.runner;

public record SelectorItem<T>(String name, T item, boolean enabled, boolean selected) {

    static <T> SelectorItem<T> of(String name, T item) {
        return of(name, item, true, false);
    }

    static <T> SelectorItem<T> of(String name, T item, boolean enabled, boolean selected) {
        return new SelectorItem<>(name, item, enabled, selected);
    }

}
