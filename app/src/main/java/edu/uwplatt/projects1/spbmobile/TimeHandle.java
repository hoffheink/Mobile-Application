package edu.uwplatt.projects1.spbmobile;

import android.support.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Bear on 3/12/2018.
 */

public class TimeHandle
{
    public static String getUTCTime(@NonNull Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T' HH:mm:ss.SSS", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }
}
