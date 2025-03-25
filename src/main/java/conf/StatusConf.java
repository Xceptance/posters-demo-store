package conf;

import java.util.Map;
import com.google.inject.Singleton;

@Singleton
public class StatusConf {
    
    // enabling this introduces mixups between the categories
    private boolean categoriesBroken = false;

    public void getStatus(final Map<String, Object> status)
    {
        status.put("categoriesBroken", categoriesBroken);
    }

    public void disableAll()
    {
        this.categoriesBroken = false;
    }

    public void setCategoriesBroken(boolean breakCategories)
    {
        this.categoriesBroken = breakCategories;
    }
}
