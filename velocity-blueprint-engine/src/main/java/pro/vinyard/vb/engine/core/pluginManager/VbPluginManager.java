package pro.vinyard.vb.engine.core.pluginManager;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.pf4j.PluginWrapper;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.plugins.api.VelocityBlueprintExtension;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class VbPluginManager {

    private final EnvironmentManager environmentManager;

    public VbPluginManager(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    public org.pf4j.PluginManager initPlugins(VelocityContext velocityContext) {
        PluginManager pluginManager = new PluginManager(environmentManager.getPluginDirectory().toPath());

        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        log.info("Plugin started : {}", pluginManager.getStartedPlugins().stream().map(PluginWrapper::getPluginId).collect(Collectors.joining(", ")));

        List<VelocityBlueprintExtension> plugins = pluginManager.getExtensions(VelocityBlueprintExtension.class);

        log.info("Extensions found : {}", plugins.stream().map(p -> p.getClass().getName()).collect(Collectors.joining(", ")));

        plugins.forEach(p -> p.init(velocityContext));

        return pluginManager;
    }

    public void unloadPlugins(org.pf4j.PluginManager pluginManager) {
        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();
        log.info("Stopped plugins.");
    }
}
