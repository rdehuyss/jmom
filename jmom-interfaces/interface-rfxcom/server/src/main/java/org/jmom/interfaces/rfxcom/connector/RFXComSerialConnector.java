package org.jmom.interfaces.rfxcom.connector;


import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class RFXComSerialConnector implements RFXComConnector {

    private static final Logger logger = LoggerFactory.getLogger(RFXComSerialConnector.class);

    private List<RFXComEventListener> listeners = new ArrayList<RFXComEventListener>();


    private String usbDeviceName;
    private InputStream in = null;
    private OutputStream out = null;
    private SerialPort serialPort = null;
    private Thread readerThread = null;

    public RFXComSerialConnector(String usbDeviceName) {
        this.usbDeviceName = usbDeviceName;
    }

    @Override
    public void connect() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
        logger.info("Connecting to RFXCOM [serialPort='{}' ].", new Object[]{serialPort});

        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(usbDeviceName);

        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.enableReceiveThreshold(1);
        serialPort.disableReceiveTimeout();

        in = serialPort.getInputStream();
        out = serialPort.getOutputStream();

        out.flush();
        if (in.markSupported()) {
            in.reset();
        }

        readerThread = new SerialReader(in);
        readerThread.start();
    }

    @Override
    public void disconnect() {
        logger.debug("Disconnecting");

        if (readerThread != null) {
            logger.debug("Interrupt serial listener");
            readerThread.interrupt();
        }

        if (out != null) {
            logger.debug("Close serial out stream");
            try {
                out.close();
            } catch (IOException e) {
                //we don't care
            }
        }
        if (in != null) {
            logger.debug("Close serial in stream");
            try {
                in.close();
            } catch (IOException e) {
                //we don't care
            }
        }

        if (serialPort != null) {
            logger.debug("Close serial port");
            serialPort.close();
        }

        readerThread = null;
        serialPort = null;
        out = null;
        in = null;

        logger.debug("Closed");
    }


    @Override
    public void sendMessage(byte[] data) throws IOException {
        out.write(data);
        out.flush();
    }

    public synchronized void addEventListener(RFXComEventListener rfxComEventListener) {
        listeners.add(rfxComEventListener);
    }

    public synchronized void removeEventListener(RFXComEventListener listener) {
        listeners.remove(listener);
    }

    public class SerialReader extends Thread {
        boolean interrupted = false;
        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        @Override
        public void interrupt() {
            interrupted = true;
            super.interrupt();
            try {
                in.close();
            } catch (IOException e) {
            } // quietly close
        }

        public void run() {
            final int dataBufferMaxLen = Byte.MAX_VALUE;

            byte[] dataBuffer = new byte[dataBufferMaxLen];

            int msgLen = 0;
            int index = 0;
            boolean start_found = false;

            System.out.println("Data listener started");

            try {

                byte[] tmpData = new byte[20];
                int len = -1;

                while ((len = in.read(tmpData)) > 0 && interrupted != true) {

                    byte[] logData = Arrays.copyOf(tmpData, len);
                    logger.trace("Received data (len={}): {}", len, DatatypeConverter.printHexBinary(logData));

                    for (int i = 0; i < len; i++) {

                        if (index > dataBufferMaxLen) {
                            // too many bytes received, try to find new start
                            start_found = false;
                        }

                        if (start_found == false && tmpData[i] > 0) {

                            start_found = true;
                            index = 0;
                            dataBuffer[index++] = tmpData[i];
                            msgLen = tmpData[i] + 1;

                        } else if (start_found) {
                            dataBuffer[index++] = tmpData[i];
                            if (index == msgLen) {
                                // whole message received, send an event

                                byte[] msg = new byte[msgLen];

                                for (int j = 0; j < msgLen; j++) {
                                    msg[j] = dataBuffer[j];
                                }

                                RFXComMessageReceivedEvent event = new RFXComMessageReceivedEvent(this);

                                try {
                                    System.out.println("Notifying event listeners");
                                    Iterator<RFXComEventListener> iterator = listeners.iterator();
                                    while (iterator.hasNext()) {
                                        iterator.next().packetReceived(event, msg);
                                    }

                                } catch (Exception e) {
                                    System.out.println("Event listener invoking error");
                                }

                                // find new start
                                start_found = false;
                            }
                        }
                    }
                }
            } catch (InterruptedIOException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted via InterruptedIOException");
            } catch (IOException e) {
                logger.error("Reading from serial port failed", e);
            }

            logger.debug("Data listener stopped");
        }
    }
}