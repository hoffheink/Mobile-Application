package edu.uwplatt.projects1.spbmobile.Lambda;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import edu.uwplatt.projects1.spbmobile.AsyncTaskResult;
import edu.uwplatt.projects1.spbmobile.MainActivity;

public class LambdaPlatform {
    private static final String TAG = LambdaPlatform.class.getCanonicalName();
    private static final String CHARACTER_SET = "UTF-8";

    public LambdaPlatform() {
    }

    public AsyncTaskResult invokeLambdaFunction(String functionName, String message, CognitoCachingCredentialsProvider credentialsProvider) {
        try {
            LambdaInvoker lambdaInvoker = new LambdaInvoker(functionName, credentialsProvider, ByteBuffer.wrap(message.getBytes()));
            return lambdaInvoker.execute().get();
        } catch (Exception e) {
            AsyncTaskResult result = new AsyncTaskResult(e);
            Log.e(TAG, "InvokeFailed", e);
            return result;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LambdaInvoker extends AsyncTask<Void, Void, AsyncTaskResult<String>> {
        private InvokeRequest invokeRequest;
        private CognitoCachingCredentialsProvider credentialsProvider;

        LambdaInvoker(String functionName,
                      @NonNull CognitoCachingCredentialsProvider credentialsProvider,
                      ByteBuffer payload) {
            invokeRequest = new InvokeRequest();

            String newFunctionName = createFunctionName(functionName);

            invokeRequest.setFunctionName(newFunctionName);
            invokeRequest.setPayload(payload);
            this.credentialsProvider = credentialsProvider;
        }

        @NonNull
        private String createFunctionName(String functionName) {
            return "arn:aws:lambda:" + MainActivity.region.toString().toLowerCase()
                    .replace("_", "-") + ":955967187114:function:" + functionName;
        }

        private Regions getRegion() throws Exception {
            switch (MainActivity.region) {
                case US_EAST_1:
                    return Regions.US_EAST_1;
                case US_EAST_2:
                    return Regions.US_EAST_2;
                default:
                    throw new Exception("Cannot resolve region: " + MainActivity.region.toString());
            }
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids) {
            try {
                AWSLambdaClient awsLambdaClient = new AWSLambdaClient(credentialsProvider);
                awsLambdaClient.setRegion(Region.getRegion(getRegion()));
                ByteBuffer buffer = awsLambdaClient.invoke(invokeRequest).getPayload();
                return new AsyncTaskResult<>(byteBufferToString(buffer,
                        Charset.forName(CHARACTER_SET)));
            } catch (Exception e) {
                Log.e(TAG, "LambdaInvokeFailed", e);
                return new AsyncTaskResult<>(e);
            }
        }

        @NonNull
        private String byteBufferToString(ByteBuffer buffer, Charset charset) {
            byte[] bytes;
            if (buffer.hasArray())
                bytes = buffer.array();
            else {
                bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
            }
            return new String(bytes, charset);
        }
    }
}