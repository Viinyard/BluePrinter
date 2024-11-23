package dev.vinyard.bp.plugins.api;

import org.pf4j.Plugin;

public abstract class BluePrinterPlugin extends Plugin {

    protected final PluginContext context;

    protected BluePrinterPlugin(PluginContext context) {
        super();

        this.context = context;
    }
}
