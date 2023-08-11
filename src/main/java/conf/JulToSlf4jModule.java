package conf;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.AbstractModule;

public class JulToSlf4jModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
