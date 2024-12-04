package dev.vinyard.blueprinter.core.prompt;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

import java.util.List;

@Data
@AllArgsConstructor
@KeySpace("prompt")
public class Prompt {

    @Id
    private String value;

    private List<String> selectedValues;
}
