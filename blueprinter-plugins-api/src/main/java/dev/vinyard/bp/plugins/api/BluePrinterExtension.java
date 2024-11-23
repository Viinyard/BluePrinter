package dev.vinyard.bp.plugins.api;

import org.apache.velocity.VelocityContext;
import org.pf4j.ExtensionPoint;

public interface BluePrinterExtension extends ExtensionPoint {

    void init(VelocityContext context);

}
