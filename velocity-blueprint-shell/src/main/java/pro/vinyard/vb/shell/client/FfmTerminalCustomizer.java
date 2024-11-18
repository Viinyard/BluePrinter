package pro.vinyard.vb.shell.client;

import org.jline.terminal.TerminalBuilder;
import org.springframework.shell.boot.TerminalCustomizer;
import org.springframework.stereotype.Component;

@Component
public class FfmTerminalCustomizer implements TerminalCustomizer {

    @Override
    public void customize(TerminalBuilder builder) {
        // builder.ffm(false);
    }
}
