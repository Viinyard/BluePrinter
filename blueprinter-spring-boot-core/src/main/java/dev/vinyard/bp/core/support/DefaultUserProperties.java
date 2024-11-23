package dev.vinyard.bp.core.support;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is responsible for managing user properties.
 * <p>User properties are stored in the user's home directory in a file named "application.xml".</p>
 * <p>Properties are stored in XML format.</p>
 * <p>Properties are loaded from the file when the application starts and saved to the file when the application stops.</p>
 */
@Slf4j
public class DefaultUserProperties implements UserProperties {

    private final ApplicationHome home = new ApplicationHome(getClass());

    private final Properties properties = new Properties();

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    @PostConstruct
    public void init() {
        log.info("User properties file : {}", home.getDir());
        File file = new File(home.getDir(), "application.xml");

        if (!file.exists())
            return;

        try (InputStream inputStream = new FileInputStream(file)) {
            log.info("Loading external properties file : {}", file.getName());
            this.properties.loadFromXML(inputStream);
        } catch (Exception e) {
            log.warn("Cannot load external properties file.", e);
        }
    }

    @PreDestroy
    public void destroy() {
        File file = new File(home.getDir(), "application.xml");

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.storeToXML(fileOutputStream, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
