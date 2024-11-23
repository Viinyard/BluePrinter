package dev.vinyard.bp.autoconfigure;

import dev.vinyard.bp.core.support.UserProperties;

import java.util.HashMap;

public class InMemoryUserProperties implements UserProperties {

    private final HashMap<String, String> properties = new HashMap<>();

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
}
