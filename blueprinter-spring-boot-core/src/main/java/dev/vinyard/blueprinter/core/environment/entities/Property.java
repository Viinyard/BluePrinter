package dev.vinyard.blueprinter.core.environment.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Property {

    @XmlAttribute
    private String key;

    @XmlValue
    private String value;

}