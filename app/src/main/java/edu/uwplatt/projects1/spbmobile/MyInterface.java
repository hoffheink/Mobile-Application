package edu.uwplatt.projects1.spbmobile;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/**
 * Created by Bear on 12/6/2017.
 */

public interface MyInterface
{
    @LambdaFunction(functionName = "arn:aws:lambda:us-east-2:955967187114:function:iot-app-register-device", invocationType = "RequestResponse")
    String AndroidBackendLambdaFunction(String s);//String Json
}