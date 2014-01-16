package conf;

import ninja.ebean.NinjaEbeanModule;

import com.google.inject.AbstractModule;

import controllers.JobController;

public class Module extends AbstractModule
{

    protected void configure()
    {
        // install ebean module
        install(new NinjaEbeanModule());
        // bind configuration class
        bind(PosterConstants.class);
        requestStaticInjection(PosterConstants.class);
        // bind starter class
        bind(JobController.class);
        // bind scheduler class
        bind(Scheduler.class);
    }
}
