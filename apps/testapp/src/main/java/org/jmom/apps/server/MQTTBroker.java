package org.jmom.apps.server;

import org.dna.mqtt.moquette.server.Server;

import java.io.IOException;

public class MQTTBroker {

    public static void main(String[] args) throws IOException {
        new Server().startServer();

        System.out.println("MQTT Broker started");
    }
}
