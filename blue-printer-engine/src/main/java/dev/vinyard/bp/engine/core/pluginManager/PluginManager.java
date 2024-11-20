package dev.vinyard.bp.engine.core.pluginManager;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginManager;

import java.nio.file.Path;

@Slf4j
public class PluginManager extends DefaultPluginManager {

    public PluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected org.pf4j.PluginFactory createPluginFactory() {
        return new PluginFactory();
    }
}
