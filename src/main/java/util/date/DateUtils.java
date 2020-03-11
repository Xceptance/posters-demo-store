package util.date;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date utility class.
 * 
 * @author sebastianloob
 */
public class DateUtils
{

    /**
     * Returns the current date.
     * 
     * @return
     */
    public static String getCurrentDate()
    {
        final Date dt = new Date();
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(dt);
    }
}
