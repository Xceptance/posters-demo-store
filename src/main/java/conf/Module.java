package conf;

import ninja.ebean.NinjaEbeanModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import controllers.JobController;

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
    }
}
