package edu.uwplatt.projects1.spbmobile.Appliance.UIComponents;

class RegisterDeviceFormat {
    private final String thingId;
    private final String thingPin;
    private final String deviceToken;
    private final String subscriptionArn;

    RegisterDeviceFormat(String thingId,
                         String thingPin,
                         String deviceToken,
                         String subscriptionArn) {
        this.thingId = thingId;
        this.thingPin = thingPin;
        this.deviceToken = deviceToken;
        this.subscriptionArn = subscriptionArn;
    }
}