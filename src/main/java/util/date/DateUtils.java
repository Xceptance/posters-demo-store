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
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(dt);
    }
}
