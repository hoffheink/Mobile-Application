package edu.uwplatt.projects1.spbmobile.Lambda;

import android.os.AsyncTask;
import android.util.Log;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import edu.uwplatt.projects1.spbmobile.AsyncTaskResult;

public class LambdaPlatform
{
    private static final String TAG = LambdaPlatform.class.getCanonicalName();
    private static final String CHARACTER_SET = "UTF-8";

    public LambdaPlatform(){}

    public AsyncTaskResult<String> invokeLambdaFunction(String functionName, String message, CognitoCachingCredentialsProvider credentialsProvider)
    {
        try
        {
            LambdaInvoker lambdaInvoker = new LambdaInvoker(functionName, credentialsProvider, ByteBuffer.wrap(message.getBytes()));
            AsyncTaskResult<String> result = lambdaInvoker.execute().get();
            return result;
        }
        catch (Exception e)
        {
            AsyncTaskResult<String> result = new AsyncTaskResult(e);
            Log.e(TAG, "InvokeFailed", e);
            return result;
        }
    }

    private class LambdaInvoker extends AsyncTask<Void, Void, AsyncTaskResult<String> >
    {
        private InvokeRequest invokeRequest;
        private CognitoCachingCredentialsProvider credentialsProvider;
        private Region region;

        public LambdaInvoker(String lambdaFunction, CognitoCachingCredentialsProvider credentialsProvider, ByteBuffer payload)
        {
            invokeRequest = new InvokeRequest();
            invokeRequest.setFunctionName(lambdaFunction.toString());
            invokeRequest.setPayload(payload);
            this.credentialsProvider = credentialsProvider;
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids)
        {
            try
            {
                AWSLambdaClient awsLambdaClient = new AWSLambdaClient(credentialsProvider);
                awsLambdaClient.setRegion(Region.getRegion(Regions.US_EAST_1));//Todo, unhardcode this
                ByteBuffer buffer = awsLambdaClient.invoke(invokeRequest).getPayload();
                return new AsyncTaskResult<String>(byteBufferToString(buffer, Charset.forName(CHARACTER_SET)));
            }
            catch(Exception e)
            {
                Log.e(TAG, "LambdaInvokeFailed", e);
                return new AsyncTaskResult<>(e);
            }
        }

        private String byteBufferToString(ByteBuffer buffer, Charset charset)
        {
            byte[] bytes;
            if (buffer.hasArray())
                bytes = buffer.array();
            else
            {
                bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
            }
            return new String(bytes, charset);
        }
    }
}