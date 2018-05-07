package edu.uwplatt.projects1.spbmobile;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.List;

import edu.uwplatt.projects1.spbmobile.Lambda.FirebaseTokenLambdaFormat;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaFunction;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaPlatform;

class ListenActivities extends Thread
{
    boolean exit = false;
    ActivityManager am = null;
    Context context = null;

    public ListenActivities(Context context)
    {
        context = context;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void run()
    {
        Looper.prepare();
        while(!exit)
        {
            // get the info from the currently running task
            List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(MAX_PRIORITY);
            String activityName = taskInfo.get(0).topActivity.getClassName();
            Log.d("topActivity", "CURRENT Activity ::"
                    + activityName);
            if (activityName.equals("com.android.packageinstaller.UninstallerActivity")) {
                // User has clicked on the Uninstall button under the Manage Apps settings

                //do whatever pre-uninstallation task you want to perform here
                // show dialogue or start another activity or database operations etc..etc..

                //context.startActivity(new Intent(context, PreUninstallActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Gson gson = new Gson();
                FirebaseTokenLambdaFormat uninstallMessageFormat = new FirebaseTokenLambdaFormat(FirebaseInstanceId.getInstance().getToken());
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(context, "us-east-1:273c20ea-e478-4c5d-8adf-8f46402a066b", Regions.US_EAST_1); //Hard coded in.
                LambdaPlatform lambdaPlatform = new LambdaPlatform();
                AsyncTaskResult<String> response = lambdaPlatform.invokeLambdaFunction(LambdaFunction.REMOVE_NOTIFICATION, gson.toJson(uninstallMessageFormat), credentialsProvider);
                Log.d("Uninstall: ", response.getResult());

                exit = true;
                Toast.makeText(context, "Done with preuninstallation tasks... Exiting Now", Toast.LENGTH_SHORT).show();
            }
            else if(activityName.equals("com.android.settings.ManageApplications"))
            {
                // back button was pressed and the user has been taken back to Manage Applications window
                // we should close the activity monitoring now
                exit=true;
            }
        }
        Looper.loop();
    }
}