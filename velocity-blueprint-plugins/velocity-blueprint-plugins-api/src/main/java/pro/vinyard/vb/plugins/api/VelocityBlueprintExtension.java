package pro.vinyard.vb.plugins.api;

import org.apache.velocity.VelocityContext;
import org.pf4j.ExtensionPoint;

public interface VelocityBlueprintExtension extends ExtensionPoint {

    void init(VelocityContext context);

}
