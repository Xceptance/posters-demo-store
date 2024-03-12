package conf;

import ninja.NinjaDefault;

import com.google.inject.Inject;

import ninja.Context;
import ninja.Result;
import ninja.Results;

public class Ninja extends NinjaDefault{

     @Inject
    PosterConstants xcpConf;

    // Customize the 404 error handling

    @Override
    public Result getNotFoundResult(Context context) {
        return Results.html().render(context).template(xcpConf.NOT_FOUND_404);

    }
    
}
