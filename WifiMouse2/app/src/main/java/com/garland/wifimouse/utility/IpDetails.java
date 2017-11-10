package com.garland.wifimouse.utility;

/**
 * Created by lemon on 9/13/2017.
 */

public class IpDetails {
    private final String ipAddress;
    private final String deviceName;
    private final String contentDescription;

    public IpDetails(String ipAddress, String deviceName, String contentDescription) {
        this.ipAddress = ipAddress;
        this.deviceName = deviceName;
        this.contentDescription = contentDescription;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getContentDescription() {
        return contentDescription;
    }
}
