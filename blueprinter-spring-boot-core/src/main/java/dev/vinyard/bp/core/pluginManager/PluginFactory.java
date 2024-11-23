package dev.vinyard.bp.core.pluginManager;

import dev.vinyard.bp.plugins.api.PluginContext;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

@Slf4j
public class PluginFactory extends DefaultPluginFactory {

    @Override
    public Plugin create(PluginWrapper pluginWrapper) {
        PluginContext context = new PluginContext(pluginWrapper.getRuntimeMode());
        try {
            Class<?> pluginClass = pluginWrapper.getPluginClassLoader().loadClass(pluginWrapper.getDescriptor().getPluginClass());
            return (Plugin) pluginClass.getConstructor(PluginContext.class).newInstance(context);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}

