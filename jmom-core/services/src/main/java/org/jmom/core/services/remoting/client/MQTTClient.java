package org.jmom.core.services.remoting.client;

import com.google.common.util.concurrent.Service;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public interface MQTTClient extends Service {

    void addMQTTCallback(MqttCallback callback);

}
