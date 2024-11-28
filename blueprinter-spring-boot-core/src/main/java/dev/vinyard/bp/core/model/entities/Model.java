package dev.vinyard.bp.core.model.entities;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Model {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private Setup setup;

    @XmlElement
    private Properties properties;

    @XmlElement
    private Directives directives;

    @XmlElement
    private Prompts prompts;

}