package edu.uwplatt.projects1.spbmobile.Firebase;

import java.util.LinkedHashMap;

/**
 * This class is used to parse the Json message from the notification.
 */
class NotificationPayload {
    private String deviceName;
    private LinkedHashMap<String, String> reported;

    /**
     * Get the name of a device.
     *
     * @return return the name of a device as a string.
     */
    String getDeviceName() {
        return this.deviceName;
    }

    /**
     * Gets the map of a component and state change.
     *
     * @return a map of strings.
     */
    LinkedHashMap<String, String> getReportedChanges() {
        return reported;
    }
}