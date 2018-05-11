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
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaFunctionNames;

/**
 * This class is used to oversee methods for the "long-term" storage of data.
 */
class SimpleStorageSystem {
    private static final String TAG = "SimpleStorageSystem";

    /**
     * This attempts to read from a file to find the subscriptionArn, if no subscriptionArn
     * is found, it attempts to generate one and save it to a text file.
     *
     * @param context
     * @param account
     * @param region
     */
    void saveSubArn(Context context, GoogleSignInAccount account,
                    CloudDatasource.RegionEnum region) {
        try {
            String filePath = context.getFilesDir().getPath() + "/subArn.txt";
            String tempStr = "";
            File f = new File(filePath);
            f.createNewFile();
            Scanner scanner = new Scanner(f);
            if (scanner.hasNextLine())
                tempStr = scanner.nextLine();
            String[] subArnParse = tempStr.split(" ");
            if (subArnParse.length < 2) {
                com.google.gson.Gson gson = new Gson();
                FirebaseTokenLambdaFormat firebaseTokenLambdaFormat =
                        new FirebaseTokenLambdaFormat(FirebaseInstanceId.getInstance().getToken());
                String pay = gson.toJson(firebaseTokenLambdaFormat);

                CloudDatasource.subscriptionArn = CloudDatasource.getInstance(context, account,
                        region).invokeLambda(LambdaFunctionNames.NOTIFICATION_INIT, pay).getResult()
                        .toString().replace("\"", "");

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context
                        .openFileOutput("subArn.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("subscriptionArn " + CloudDatasource.getInstance(
                        context, account, region).getSubscriptionArn());
                outputStreamWriter.close();
            } else
                CloudDatasource.subscriptionArn = subArnParse[1];
            Log.d(TAG, CloudDatasource.getInstance(context, account, region)
                    .getCognitoCachingCredentialsProvider().toString());
            Log.d(TAG, "File path: " + context.getFilesDir().getPath());
            Log.d(TAG, "Firebase Token: " + FirebaseInstanceId.getInstance().getToken());
            Log.d(TAG, "SubscriptionArn: " + CloudDatasource.getInstance(context, account,
                    region).getSubscriptionArn());
            com.google.gson.Gson gson = new Gson();
            FirebaseTokenLambdaFormat firebaseTokenLambdaFormat = new FirebaseTokenLambdaFormat(
                    FirebaseInstanceId.getInstance().getToken());
            CloudDatasource.getInstance(context, account, region).invokeLambda(
                    LambdaFunctionNames.REMOVE_NOTIFICATION,
                    gson.toJson(firebaseTokenLambdaFormat));
        } catch (Exception e) {
            Log.e(TAG, "SaveSubArn", e);
        }
    }
}