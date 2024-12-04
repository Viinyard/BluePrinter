package dev.vinyard.blueprinter.core.support;

public interface UserProperties {

    String HOME_DIRECTORY = "homeDirectory";

    String getProperty(String key);

    void setProperty(String key, String value);
}
