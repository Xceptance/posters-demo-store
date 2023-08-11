package util.standalone;

import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import ninja.standalone.NinjaJetty;

/**
 * A specialization of the {@link NinjaJetty} starter class to customize the underlying Jetty.
 * <p>
 * An alternative approach would be to provide a full Jetty configuration via system property
 * "ninja.jetty.configuration" as outlined
 * <a href="http://www.ninjaframework.org/documentation/deployment/ninja_standalone.html">here</a>. But for now, this
 * here is simpler.
 */
public class PostersMain extends NinjaJetty
{
    public static void main(final String[] args)
    {
        // create new instance and run it
        new PostersMain().run();
    }

    /**
     * {@inheritDoc}
     */
    protected void doConfigure() throws Exception
    {
        super.doConfigure();

        // add GZIP support to Jetty
        final GzipHandler gzip = new GzipHandler();
        jetty.insertHandler(gzip);
    }
}
