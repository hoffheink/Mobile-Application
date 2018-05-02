package edu.uwplatt.projects1.spbmobile.Appliance.UIComponents;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaFunction;
import edu.uwplatt.projects1.spbmobile.MainActivity;

public class RemoveApplianceFragment
{
    private final GoogleSignInAccount account;
    private final Context context;

    public RemoveApplianceFragment(GoogleSignInAccount inAccount, Context inContext)
    {
        account = inAccount;
        context = inContext;
    }

    public void RemoveAppliance(String thingId)
    {
        Gson gson = new Gson();
        RemoveDeviceFormat removeDeviceFormat = new RemoveDeviceFormat(thingId, FirebaseInstanceId.getInstance().getToken());
        CloudDatasource.getInstance(context, account, MainActivity.region).invokeLambda(LambdaFunction.REMOVE_DEVICE, gson.toJson(removeDeviceFormat));
    }
}