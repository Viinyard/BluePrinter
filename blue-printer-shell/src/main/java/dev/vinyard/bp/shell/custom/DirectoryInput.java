package dev.vinyard.bp.shell.custom;

import lombok.extern.slf4j.Slf4j;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.PathInput;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Input component to read a directory path
 */
@Slf4j
public class DirectoryInput extends PathInput {

    public DirectoryInput(Terminal terminal, String name) {
        super(terminal, name);
    }


    @Override
    protected boolean read(BindingReader bindingReader, KeyMap<String> keyMap, PathInputContext context) {
        String operation = bindingReader.readBinding(keyMap);
        log.debug("Binding read result {}", operation);
        if (operation == null) {
            return true;
        }
        String input;
        switch (operation) {
            case OPERATION_CHAR:
                String lastBinding = bindingReader.getLastBinding();
                input = context.getInput();
                if (input == null) {
                    input = lastBinding;
                } else {
                    input = input + lastBinding;
                }
                context.setInput(input);
                checkPath(input, context);
                break;
            case OPERATION_BACKSPACE:
                input = context.getInput();
                if (StringUtils.hasLength(input)) {
                    input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
                }
                context.setInput(input);
                checkPath(input, context);
                break;
            case OPERATION_EXIT:
                if (StringUtils.hasText(context.getInput())) {
                    context.setResultValue(Paths.get(context.getInput()));
                }
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Check if the path is a directory
     *
     * @param path the path to check
     * @param context the context to update
     */
    private void checkPath(String path, PathInputContext context) {
        if (!StringUtils.hasText(path)) {
            context.setMessage(null);
            return;
        }
        Path p = resolvePath(path);
        boolean isDirectory = Files.isDirectory(p);
        if (isDirectory) {
            context.setMessage("Directory ok", TextComponentContext.MessageLevel.INFO);
        } else {
            context.setMessage("Not a directory", TextComponentContext.MessageLevel.ERROR);
        }
    }
}
