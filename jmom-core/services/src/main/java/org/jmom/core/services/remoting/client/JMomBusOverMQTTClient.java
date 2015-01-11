package org.jmom.core.services.remoting.client;

import com.google.common.eventbus.Subscribe;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.eda.*;

public class JMomBusOverMQTTClient implements MQTTCallback {

    public static final String TOPIC_BUS = "/jMomBus";
    private final JMomBus jMomBus;
    private final MQTTClientService mqttClientService;

    public JMomBusOverMQTTClient(JMomBus jMomBus, MQTTClientService mqttClientService) {
        this.jMomBus = jMomBus;
        this.mqttClientService = mqttClientService;
        this.jMomBus.register(this);
        this.mqttClientService.addMQTTCallback(this);
    }

    @Subscribe
    public void onChange(Message message) {
        if(message instanceof LocalMessage) {
            return;
        }

        try {
            mqttClientService.sendMessage(TOPIC_BUS, message);
        } catch (TransportException e) {
            if(message instanceof Command) {
                jMomBus.post(new ErrorMessage((Command)message, "Could not transport command"));
            }
            e.printStackTrace();
        }
    }
    @Override
    public <T extends Message> void onMessageArrived(String topic, T object) {
        if(TOPIC_BUS.equals(topic)) {
            jMomBus.post(object);
        }
    }
}
