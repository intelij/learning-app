package org.stepic.droid.web

import org.stepic.droid.model.ClientType
import org.stepic.droid.model.Device

class DeviceRequest(token: String, description: String) {
    private val device: Device

    init {
        device = Device(0, token, 0, description, ClientType.Android)
    }
}
