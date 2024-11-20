package dev.vinyard.bp.engine.core.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import dev.vinyard.bp.engine.core.exception.BluePrinterException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/**
 * File utilities
 */
@Slf4j
public class FileUtils {

    /**
     * Create a file in the specified directory
     *
     * @param file        Internal file path to be copied
     * @param destination The destination file
     * @return The result message
     */
    public static File copyFile(String file, File destination) {
        try (InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(file)) {
            if (inputStream == null)
                throw new RuntimeException("Cannot find file " + file);
            Files.copy(inputStream, destination.toPath());

            return destination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * List all files in the directory with the specified extensions
     *
     * @param directory  The directory
     * @param extensions The extensions
     * @return The list of files with the specified extensions in the directory
     */
    public static List<File> listFiles(File directory, String... extensions) {
        return org.apache.commons.io.FileUtils.listFiles(directory, extensions, true).stream().toList();
    }

    /**
     * Load a configuration file
     * <p>Load the XML content with the given environment class and schema location and check if the content is a valid XML given the schema.</p>
     *
     * @param source           The XML source (File or String)
     * @param environmentClass The environment class
     * @param schemaLocation   The schema location
     * @param <T>              The environment class
     * @return The environment
     * @throws BluePrinterException if an error occurs during loading
     */
    private static <T> T loadConfiguration(Source source, Class<T> environmentClass, String schemaLocation) throws BluePrinterException {
        try {
            JAXBContext context = JAXBContext.newInstance(environmentClass);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "true");

            Source schemaFile = new StreamSource(FileUtils.class.getClassLoader().getResourceAsStream(schemaLocation));
            Schema schema = schemaFactory.newSchema(schemaFile);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);

            return Optional.of(unmarshaller.unmarshal(source)).filter(environmentClass::isInstance).map(environmentClass::cast).orElseThrow(() -> new BluePrinterException("Invalid configuration file"));
        } catch (JAXBException | SAXException e) {
            throw new BluePrinterException(Optional.ofNullable(e.getCause()).map(Throwable::getMessage).or(() -> Optional.of(e).map(Exception::getMessage)).orElse("unknown"), e);
        }
    }

    /**
     * Load a configuration file
     * <p>Load the XML file with the given environment class and schema location and check if the file is a valid XML file given the schema.</p>
     *
     * @param xmlFile          The XML file
     * @param environmentClass The environment class
     * @param schemaLocation   The schema location
     * @param <T>              The environment class
     * @return The environment
     * @throws BluePrinterException if an error occurs during loading
     */
    public static <T> T loadConfigurationFile(File xmlFile, Class<T> environmentClass, String schemaLocation) throws BluePrinterException {
        return loadConfiguration(new StreamSource(xmlFile), environmentClass, schemaLocation);
    }

    /**
     * Load a configuration file
     * <p>Load the XML string with the given environment class and schema location and check if the file is a valid XML file given the schema.</p>
     *
     * @param xml              The XML string
     * @param environmentClass The environment class
     * @param schemaLocation   The schema location
     * @param <T>              The environment class
     * @return The environment
     * @throws BluePrinterException if an error occurs during loading
     */
    public static <T> T loadConfigurationFile(String xml, Class<T> environmentClass, String schemaLocation) throws BluePrinterException {
        return loadConfiguration(new StreamSource(new StringReader(xml)), environmentClass, schemaLocation);
    }
}
