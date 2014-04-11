package gen.scripting.posters.functional.modules;

import com.xceptance.xlt.api.engine.scripting.AbstractWebDriverModule;

/**
 * Searches the specified term.
 */
public class Search extends AbstractWebDriverModule
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCommands(final String...parameters) throws Exception
    {
        final String searchTerm = parameters[0];
        // Enter the search phrse into the input
        type("id=searchText", searchTerm);
        // Cick the the search button to submit
        clickAndWait("id=btnSearch");

    }
}