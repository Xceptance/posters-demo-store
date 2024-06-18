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

import ninja.ebean.NinjaEbeanModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import controllers.JobController;
import models.LuceneSearch;
import models.SearchEngine;

/**
 * Ninja uses Guice as injection tool. Define your bindings in this class, which you want to use via @{@link Inject} in
 * another class.
 * 
 * @author sebastianloob
 */
public class Module extends AbstractModule
{
    @Override
    protected void configure()
    {
        // install jul-to-SLF4j Bridge
        install(new JulToSlf4jModule());
        // install ebean module
        install(new NinjaEbeanModule());
        // bind configuration class
        bind(PosterConstants.class);
        requestStaticInjection(PosterConstants.class);
        // bind starter class
        bind(JobController.class);
        // bind scheduler class
        bind(Scheduler.class);
        // bind search engine
        bind(SearchEngine.class).to(LuceneSearch.class).asEagerSingleton();;
    }
}
