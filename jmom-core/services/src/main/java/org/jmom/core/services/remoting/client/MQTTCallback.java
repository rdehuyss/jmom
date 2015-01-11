package org.jmom.core.services.remoting.client;

import org.jmom.core.infrastucture.eda.Message;

public interface MQTTCallback {

    public <T extends Message> void onMessageArrived(String topic, T object);
}
