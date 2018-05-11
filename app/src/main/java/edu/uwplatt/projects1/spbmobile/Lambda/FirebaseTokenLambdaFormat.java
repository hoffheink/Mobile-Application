package edu.uwplatt.projects1.spbmobile.Lambda;

/**
 * Used to format a message to send a lambda function that registers a device token.
 */
public class FirebaseTokenLambdaFormat {
    private String deviceToken;

    /**
     * Constructor to set the data in the class.
     *
     * @param instance is the firebase token instance.
     */
    public FirebaseTokenLambdaFormat(String instance) {
        deviceToken = instance;
    }
}
