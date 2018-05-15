package edu.uwplatt.projects1.spbmobile.Firebase;

/**
 * This class is used to parse the Json message from the notification.
 */
class NotificationPayload {
    private String deviceName;
    private String rawNotification;

    /**
     * Get the name of a device.
     *
     * @return return the name of a device as a string.
     */
    String getDeviceName() {
        return this.deviceName;
    }
    String getRawNotification() {
        return this.rawNotification;
    }
}