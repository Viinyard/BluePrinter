package dev.vinyard.blueprinter.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.*;

@Slf4j
public class VelocityUtils {

    public static String processTemplate(VelocityContext velocityContext, File template) throws IOException {
        StringWriter stringWriter = new StringWriter();
        Velocity.evaluate(velocityContext, stringWriter, "LOG", new FileReader(template));
        return stringWriter.toString();
    }

    /**
     * Process the template with the given velocity context.
     * A template is a string that can contain velocity code.
     *
     * @param velocityContext The velocity context to use. The context is a map of key value pairs that can be used in the templates.
     * @param template The template to process
     * @return The result of the template processing
     */
    public static String processTemplate(VelocityContext velocityContext, String template) {
        StringWriter stringWriter = new StringWriter();
        Velocity.evaluate(velocityContext, stringWriter, "LOG", new StringReader(template));
        return stringWriter.toString();
    }

    public static void processTemplate(VelocityContext velocityContext, File template, File file) {
        Velocity.init();

        try {
            FileUtils.forceMkdirParent(file);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create parent directory.", e);
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            Velocity.evaluate(velocityContext, fileWriter, "LOG", new FileReader(template));
            log.info("File {} generated.", file.getAbsolutePath());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot process template.", e);
        }
    }
}
