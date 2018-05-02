package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import edu.uwplatt.projects1.spbmobile.Lambda.FirebaseTokenLambdaFormat;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaFunction;

/**
 * This class is used to oversee methods for the "long-term" storage of data.
 */
public class SimpleStorageSystem
{
    private static final String TAG = "SimpleStorageSystem";

    /**
     * This attempts to read from a file to find the subscriptionArn, if no subscriptionArn
     * is found, it attempts to generate one and save it to a text file.
     *
     * @param context
     * @param account
     * @param region
     */
    public void saveSubArn(Context context, GoogleSignInAccount account, CloudDatasource.RegionEnum region)
    {
        try
        {
            String filePath = context.getFilesDir().getPath().toString() + "/subArn.txt";
            String tempStr = "";
            File f = new File(filePath);
            f.createNewFile();
            Scanner scanner = new Scanner(f);
            if (scanner.hasNextLine())
                tempStr = scanner.nextLine();
            String[] subArnParse = tempStr.split(" ");
            if (subArnParse.length < 2)
            {
                com.google.gson.Gson gson = new Gson();
                FirebaseTokenLambdaFormat firebaseTokenLambdaFormat = new FirebaseTokenLambdaFormat(FirebaseInstanceId.getInstance().getToken());
                String pay = gson.toJson(firebaseTokenLambdaFormat);
                //AsyncTaskResult<String> arn = CloudDatasource.getInstance(context, account, region).invokeLambda(LambdaFunction.NOTIFICATION_INIT, pay);
                CloudDatasource.getInstance(context, account, region).setSubscriptionArn(removeWorthless(CloudDatasource.getInstance(context, account, region).invokeLambda(LambdaFunction.NOTIFICATION_INIT, pay)));



                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("subArn.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("subscriptionArn " + CloudDatasource.getInstance(context, account, region).getSubscriptionArn());
                outputStreamWriter.close();
            }
            else
                CloudDatasource.getInstance(context, account, region).setSubscriptionArn(subArnParse[1]);
            Log.d(TAG, "File path: " + context.getFilesDir().getPath().toString());
            Log.d(TAG, "Firebase Token: " + FirebaseInstanceId.getInstance().getToken());
            Log.d(TAG, "SubscriptionArn: " + CloudDatasource.getInstance(context, account, region).getSubscriptionArn());
        }
        catch (Exception e)
        {
            Log.e(TAG, "SaveSubArn", e);
        }
    }

    private String removeWorthless(AsyncTaskResult<String> arn)
    {
        String str = "";
        for(int i = 0; i < arn.getResult().length(); i++)
            if(arn.getResult().charAt(i) != '\"' && arn.getResult().charAt(i) != '\"')
                str += arn.getResult().charAt(i);
        return str;
    }
}