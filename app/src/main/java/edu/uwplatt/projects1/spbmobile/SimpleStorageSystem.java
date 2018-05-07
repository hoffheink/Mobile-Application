package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import java.io.File;
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
                CloudDatasource.getInstance(context, account, region).setSubscriptionArn(
                        removeDoubleQuotationMarks(CloudDatasource.getInstance(context, account, region).invokeLambda(LambdaFunction.NOTIFICATION_INIT, pay)));

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("subArn.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("subscriptionArn " + CloudDatasource.getInstance(context, account, region).getSubscriptionArn());
                outputStreamWriter.close();
            }
            else
                CloudDatasource.getInstance(context, account, region).setSubscriptionArn(subArnParse[1]);
            Log.d(TAG,  CloudDatasource.getInstance(context, account, region).getCognitoCachingCredentialsProvider().toString());
            Log.d(TAG, "File path: " + context.getFilesDir().getPath().toString());
            Log.d(TAG, "Firebase Token: " + FirebaseInstanceId.getInstance().getToken());
            Log.d(TAG, "SubscriptionArn: " + CloudDatasource.getInstance(context, account, region).getSubscriptionArn());
        }
        catch (Exception e)
        {
            Log.e(TAG, "SaveSubArn", e);
        }
    }

    /**
     * Inspects the characters in a given string, and removes double quotation marks.
     * @param arn a asyncTaskResult that provides the string to correct.
     * @return a string of characters without quotation marks.
     */
    private String removeDoubleQuotationMarks(AsyncTaskResult<String> arn)
    {
        String str = "";
        for(int i = 0; i < arn.getResult().length(); i++)
            if(arn.getResult().charAt(i) != '\"' && arn.getResult().charAt(i) != '\"')
                str += arn.getResult().charAt(i);
        return str;
    }
}