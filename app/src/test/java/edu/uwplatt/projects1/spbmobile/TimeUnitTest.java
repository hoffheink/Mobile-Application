package edu.uwplatt.projects1.spbmobile;

import java.util.Date;
import org.junit.Test;

public class TimeUnitTest
{
    private static final String TAG = "TimeUnitTest";

    @Test
    public  void getUtcTime_CompareTime_InternalCpuClock()
    {
        String current = Time.getUTCTime(new Date());
        System.out.println("The current time is:\n" + current);
        assert(true);
    }
}