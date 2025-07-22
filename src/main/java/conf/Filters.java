package conf;

import java.util.List;

import filters.AdjustStateFilter;
import filters.LocaleFilter;
import ninja.application.ApplicationFilters;
import ninja.Filter;

public class Filters implements ApplicationFilters {

    @Override
    public void addFilters(List<Class<? extends Filter>> filters) {
        filters.add(LocaleFilter.class);
        filters.add(AdjustStateFilter.class);
    }
}
