package edu.uwplatt.projects1.spbmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UninstallHand extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // fetching package names from extras
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");

        if(packageNames!=null)
        {
            for(String packageName: packageNames){

                if(packageName!=null && packageName.equals("YOUR_APPLICATION_PACKAGE_NAME"))//Don't remember what this is; PPP?
                {
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    new ListenActivities(context).start();
                    //something be fishy

                }
            }
        }
    }

}