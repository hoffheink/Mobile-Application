package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.concurrent.ExecutionException;


public class GoogleClientWorkerThread {

    private final Context context;
    private final GoogleSignInOptions googleSignInOptions;


    public GoogleClientWorkerThread(Context context) {
        this.context = context;
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    public GoogleSignInAccount getAccount() throws ExecutionException, InterruptedException {
        SilentLoginWithGoogle silentLoginWithGoogle = new SilentLoginWithGoogle();
        return silentLoginWithGoogle.execute().get();
    }

    private class SilentLoginWithGoogle extends AsyncTask<Void, Void, GoogleSignInAccount> {

        @Override
        protected GoogleSignInAccount doInBackground(Void... voids) {
            try {
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
                GoogleSignInAccount googleSignInAccount = googleSignInClient.silentSignIn().getResult();
                return googleSignInAccount;
            } catch (Exception e) {
                Log.e("SilentLoginWithGoogle", "Error with silent login", e);
                return null;
            }
        }
    }
}