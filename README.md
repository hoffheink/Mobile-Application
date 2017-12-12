E/AndroidRuntime: FATAL EXCEPTION: AsyncTask #3
                  Process: edu.uwplatt.projects1.spbmobile, PID: 14076
                  java.lang.RuntimeException: An error occurred while executing doInBackground()
                      at android.os.AsyncTask$3.done(AsyncTask.java:309)
                      at java.util.concurrent.FutureTask.finishCompletion(FutureTask.java:354)
                      at java.util.concurrent.FutureTask.setException(FutureTask.java:223)
                      at java.util.concurrent.FutureTask.run(FutureTask.java:242)
                      at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:234)
                      at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1113)
                      at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:588)
                      at java.lang.Thread.run(Thread.java:818)
                   Caused by: com.amazonaws.services.cognitoidentity.model.NotAuthorizedException: Access to Identity 'us-east-2:c3dd3507-eed8-4bdd-aadc-f2df31a12026' is forbidden. (Service: AmazonCognitoIdentity; Status Code: 400; Error Code: NotAuthorizedException; Request ID: 59922eb8-df8b-11e7-8094-5736db89420c)
                      at com.amazonaws.http.AmazonHttpClient.handleErrorResponse(AmazonHttpClient.java:729)
                      at com.amazonaws.http.AmazonHttpClient.executeHelper(AmazonHttpClient.java:405)
                      at com.amazonaws.http.AmazonHttpClient.execute(AmazonHttpClient.java:212)
                      at com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient.invoke(AmazonCognitoIdentityClient.java:559)
                      at com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient.getCredentialsForIdentity(AmazonCognitoIdentityClient.java:389)
                      at com.amazonaws.auth.CognitoCredentialsProvider.populateCredentialsWithCognito(CognitoCredentialsProvider.java:748)
                      at com.amazonaws.auth.CognitoCredentialsProvider.startSession(CognitoCredentialsProvider.java:674)
                      at com.amazonaws.auth.CognitoCredentialsProvider.refresh(CognitoCredentialsProvider.java:611)
                      at com.amazonaws.auth.CognitoCachingCredentialsProvider.refresh(CognitoCachingCredentialsProvider.java:514)
                      at edu.uwplatt.projects1.spbmobile.CloudDatasource$task.doInBackground(CloudDatasource.java:59)
                      at edu.uwplatt.projects1.spbmobile.CloudDatasource$task.doInBackground(CloudDatasource.java:56)
                      at android.os.AsyncTask$2.call(AsyncTask.java:295)
                      at java.util.concurrent.FutureTask.run(FutureTask.java:237)
                      at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:234) 
                      at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1113) 
                      at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:588) 
                      at java.lang.Thread.run(Thread.java:818) 


# Spectrum Brands IoT Mobile App

A android-based mobile application for communicating with and controlling compatible Spectrum Brands devices.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

As of the writing of this README, the only prerequisite for using this software is Android Studio. 

WIP

### Installing

WIP

## Testing

WIP

## Deployment

The project can be built with Android Studio into an .apk file. The .apk can be downloaded and installed to a compatible android device.

## Built With

WIP

## Versioning

For this project [SemVer](http://semver.org) is usedfor versioning.

## Authors

* **Jacob Ira** - *Initial work* - [jakeira11](https://bitbucket.org/jakeira11/)
* **Nick Sosinski** - *Initial work* - [sosinskin](https://bitbucket.org/sosinskin/)
* **Kyle Hoffhein** - *Initial work* - [hoffheink](https://bitbucket.org/hoffheink/)
* **Alex Jacobs** - *Initial work* - [jacobsale](https://bitbucket.org/Jacobsale/)

## License

WIP