package org.jmom.core.services.remoting.client;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.infrastucture.bus.JMomCommandBus;
import org.jmom.core.infrastucture.bus.JMomEventBus;
import org.jmom.core.infrastucture.eda.Change;
import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.infrastucture.eda.Event;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;

import static java.lang.Integer.toString;

public class MQTTClient extends AbstractService implements Service, MqttCallback, JMomBusAware {

    private final String username;
    private final String devicename;
    private final JMomCommandBus commandBus;
    private final JMomEventBus eventBus;
    private final JMomObjectMapper objectMapper;
    private MqttAsyncClient client;

    public MQTTClient(String username, String devicename, JMomCommandBus commandBus, JMomEventBus eventBus, JMomObjectMapper objectMapper) {
        this.username = username;
        this.devicename = devicename;
        this.commandBus = commandBus;
        this.eventBus = eventBus;
        this.objectMapper = objectMapper;

        eventBus.register(this);
        commandBus.register(this);
    }

    @Override
    protected void doStart() {
        try {
            String clientId = Integer.toString((username + "/" + devicename).hashCode());
            client = new MqttAsyncClient("tcp://192.168.1.10:1883", clientId, new MemoryPersistence());
            IMqttToken connect = client.connect();
            client.setCallback(this);
            connect.waitForCompletion();

            IMqttToken subscribe = client.subscribe("/" + username + "/#", 0);
            subscribe.waitForCompletion();
            notifyStarted();
        } catch (MqttException e) {
            notifyFailed(e);
        }
    }

    @Override
    protected void doStop() {
        try {
            client.disconnect();
            client.close();
            notifyStopped();
        } catch (MqttException e) {
            notifyFailed(e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Subscribe
    public void onCommand(Command command) {
        try {
            if (!command.isTransmittedOverNetwork()) {
                System.out.println("Transmitting command message over network");
                command.setTransmittedOverNetwork();
                MqttMessage message = new MqttMessage();
                message.setQos(0);
                message.setPayload(objectMapper.writeValueAsBytes(command));
                IMqttDeliveryToken publish = client.publish("/" + username + "/" + devicename, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEvent(Event event) {
        try {
            if (!event.isTransmittedOverNetwork()) {
                System.out.println("Transmitting event message over network");
                event.setTransmittedOverNetwork();
                MqttMessage message = new MqttMessage();
                message.setQos(0);
                message.setPayload(objectMapper.writeValueAsBytes(event));
                IMqttDeliveryToken publish = client.publish("/" + username + "/" + devicename, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Received message on topic " + topic);
        if(! topic.endsWith("/" + devicename)) {
            try {
                Change change = objectMapper.readValue(message.getPayload(), Change.class);
                if (change instanceof Command) {
                    commandBus.post((Command) change);
                } else if (change instanceof Event) {
                    eventBus.post((Event) change);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
