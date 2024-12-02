package dev.vinyard.bp.core.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Directive {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String template;

    @XmlAttribute
    private Boolean override = false;

    @XmlValue
    private String value;

}