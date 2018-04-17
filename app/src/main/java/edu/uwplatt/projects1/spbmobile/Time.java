package edu.uwplatt.projects1.spbmobile;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Handle time operations.
 */
public class Time {
    /**
     * Get the current time for the UTC timezone region.
     *
     * @param date the date the object is called.
     * @return the formatted time as a string.
     */
    public static String getUTCTime(@NonNull Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T' HH:mm:ss.SSS",
                Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }
}
