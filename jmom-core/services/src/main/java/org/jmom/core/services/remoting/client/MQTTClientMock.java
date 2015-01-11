package org.jmom.core.services.remoting.client;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.eda.Message;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.controlunit.ControlUnit;

public class MQTTClientMock extends AbstractService implements MQTTClient, MqttCallback {

    private ControlUnit controlUnit;
    private final JMomBus jMomBus;
    private final JMomObjectMapper objectMapper;

    public MQTTClientMock(ControlUnit controlUnit, JMomBus jMomBus, JMomObjectMapper objectMapper) {
        this.controlUnit = controlUnit;
        this.jMomBus = jMomBus;
        this.objectMapper = objectMapper;

        jMomBus.register(this);
    }

    @Override
    protected void doStart() {
        System.out.println("MQTT Mock started");
        notifyStarted();
    }

    @Override
    protected void doStop() {
        System.out.println("MQTT Mock stopped");
        notifyStopped();
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Subscribe
    public void onChange(Message change) {
        try {
            if (!change.isTransmittedOverNetwork()) {
                System.out.println("Transmitting command message over network");
                change.setTransmittedOverNetwork();
                MqttMessage message = new MqttMessage();
                message.setQos(0);
                message.setPayload(objectMapper.writeValueAsBytes(change));
                System.out.println("Sending command to " + controlUnit.getFQN() + "; " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Received message on topic " + topic);
        if (!topic.endsWith("/" + controlUnit.getControlUnitName())) {
            try {
                Message change = objectMapper.readValue(message.getPayload(), Message.class);
                jMomBus.post(change);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void addMQTTCallback(MqttCallback callback) {

    }
}
