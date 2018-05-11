package edu.uwplatt.projects1.spbmobile.Appliance;

import com.google.firebase.iid.FirebaseInstanceId;

class RemoveDeviceFormat {
    private final String thingId;
    private final String deviceToken;

    RemoveDeviceFormat(String thingId) {
        this.thingId = thingId;
        this.deviceToken = FirebaseInstanceId.getInstance().getToken();
    }
}