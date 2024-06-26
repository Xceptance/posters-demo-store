/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
