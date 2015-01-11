package org.jmom.core.services.remoting.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractService;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.eda.ErrorMessage;
import org.jmom.core.infrastucture.eda.Message;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.controlunit.ControlUnit;

import java.util.LinkedList;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.substringAfter;

public class MQTTClientService extends AbstractService implements MqttCallback {

    private ControlUnit controlUnit;
    private JMomBus jMomBus;
    private JMomObjectMapper objectMapper;
    private MqttAsyncClient client;
    private Set<MQTTCallback> callbacks;
    private LinkedList<QueuedMessage> queuedMessages;

    public MQTTClientService(ControlUnit controlUnit, JMomBus jMomBus, JMomObjectMapper objectMapper) {
        this.controlUnit = controlUnit;
        this.jMomBus = jMomBus;
        this.objectMapper = objectMapper;
        this.callbacks = Sets.newHashSet();
        this.queuedMessages = Lists.newLinkedList();
    }

    @Override
    protected void doStart() {
        try {
            System.out.println("Starting MQTTClientService");
            String clientId = Integer.toString(controlUnit.hashCode());
            client = new MqttAsyncClient("tcp://192.168.1.32:1883", clientId, new MemoryPersistence());
            IMqttToken connect = client.connect();
            client.setCallback(this);
            connect.waitForCompletion();

            IMqttToken subscribe = client.subscribe("/" + controlUnit.getEmailAddress() + "/#", 0);
            subscribe.waitForCompletion();
            notifyStartedAndSendQueuedMessages();
        } catch (MqttException | TransportException e) {
            e.printStackTrace();
            notifyFailed(e);
            jMomBus.post(new ErrorMessage("Could not connect to MQTT server"));
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
        // Reconnect?
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if (!topic.startsWith("/" + controlUnit.getFQNWithoutPassword())) {
            try {
                String actualTopic = "/" + substringAfter(substringAfter(topic, "/" + controlUnit.getEmailAddress() + "/"), "/");
                System.out.println("New remote message arrived on topic " + actualTopic);
                Message message = objectMapper.readValue(mqttMessage.getPayload(), Message.class);
                for (MQTTCallback callback : callbacks) {
                    callback.onMessageArrived(actualTopic, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("New local message arrived on topic " + topic);
        }
    }

    public void sendMessage(String topic, Message message) throws TransportException {
        if (isRunning())
            sendMessageViaMQTT(topic, message);
        else {
            System.out.println("Adding message to queue since MQTTClient is not started yet...");
            queuedMessages.addLast(new QueuedMessage(topic, message));
        }
    }

    private void sendMessageViaMQTT(String topic, Message message) throws TransportException {
        try {
            if (!message.isTransmittedOverNetwork()) {
                message.setTransmittedOverNetwork();
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(0);
                mqttMessage.setPayload(objectMapper.writeValueAsBytes(message));
                IMqttDeliveryToken publish = client.publish("/" + controlUnit.getFQNWithoutPassword() + topic, mqttMessage);
            }
        } catch (MqttException e) {
            throw new TransportException("Could not transport message", e);
        } catch (RuntimeException e) {
            throw new TransportException("Could not serialize message", e);
        }
    }

    private void notifyStartedAndSendQueuedMessages() throws TransportException {
        System.out.println("Sending queued messages");
        while (!queuedMessages.isEmpty()) {
            QueuedMessage queuedMessage = queuedMessages.removeFirst();
            sendMessageViaMQTT(queuedMessage.topic, queuedMessage.message);
        }
        notifyStarted();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void addMQTTCallback(MQTTCallback callback) {
        callbacks.add(callback);
    }

    public void removeMQTTCallback(MQTTCallback callback) {
        callbacks.remove(callback);
    }

    private static class QueuedMessage {
        private final String topic;
        private final Message message;

        private QueuedMessage(String topic, Message message) {
            this.topic = topic;
            this.message = message;
        }
    }
}
