package gen.scripting.posters.functional.modules;

import com.xceptance.xlt.api.engine.scripting.AbstractWebDriverModule;

/**
 * Deletes all visible cookies and opens the homepage.
 */
public class OpenHomepage extends AbstractWebDriverModule
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCommands(final String...parameters) throws Exception
    {

        //
        // ~~~ OpenStartPage ~~~
        //
        startAction("OpenStartPage");
        open("/posters");
        deleteCookie("NINJA_SESSION");
        open("/posters");
        assertElementPresent("id=titleIndex");

    }
}