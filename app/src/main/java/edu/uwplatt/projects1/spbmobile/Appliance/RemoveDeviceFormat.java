package edu.uwplatt.projects1.spbmobile.Appliance;

class RemoveDeviceFormat {
    private final String thingId;
    private final String subscriptionArn;

    RemoveDeviceFormat(String thingId, String subscriptionArn) {
        this.thingId = thingId;
        this.subscriptionArn = subscriptionArn;
    }
}