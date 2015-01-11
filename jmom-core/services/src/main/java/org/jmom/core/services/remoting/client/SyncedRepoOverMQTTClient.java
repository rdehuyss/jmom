package org.jmom.core.services.remoting.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.JMomFluentIterable;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.LocalAggregateRoot;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.infrastucture.eda.Message;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.controlunit.ControlUnit;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.JMomFluentIterable.from;

public class SyncedRepoOverMQTTClient implements MQTTCallback {

    public static final String TOPIC_SYNCED_REPO = "/syncedRepo";

    private final ControlUnit controlUnit;
    private final Repo repo;
    private final MQTTClientService mqttClientService;
    private JMomObjectMapper objectMapper;
    private final JMomBus jMomBus;
    private CountDownLatch countDownLatch;

    public SyncedRepoOverMQTTClient(ControlUnit controlUnit, Repo repo, MQTTClientService mqttClientService, JMomObjectMapper objectMapper, JMomBus jMomBus) {
        this.controlUnit = controlUnit;
        this.repo = repo;
        this.mqttClientService = mqttClientService;
        this.objectMapper = objectMapper;
        this.jMomBus = jMomBus;
        this.mqttClientService.addMQTTCallback(this);
        refreshAggregateRoots();
    }

    private void refreshAggregateRoots() {
        try {
            Map<String, AggregateRoot> aggregateRoots = repo.listAggregateRoots();
            Set<AggregateRootRequest> aggregateRootRequests = from(aggregateRoots.keySet())
                    .filter(not(name -> aggregateRoots.get(name) instanceof LocalAggregateRoot))
                    .transform(name -> new AggregateRootRequest(name, aggregateRoots.get(name).getVersion()))
                    .toSet();

            MQTTRequest mqttRequest = new MQTTRequest(aggregateRootRequests);
            mqttClientService.sendMessage(TOPIC_SYNCED_REPO, mqttRequest);
            sendEmptyRepoSyncedEventIfNoResponseAfter10Seconds();
        } catch (TransportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends Message> void onMessageArrived(String topic, T object) {
        if (TOPIC_SYNCED_REPO.equals(topic)) {
            if (object instanceof MQTTRequest && controlUnit.isCentralControlUnit()) {
                respond((MQTTRequest) object);
            } else if (object instanceof MQTTResponse) {
                Set<String> updatedAggregateRoots = updateRepoAndReturnUpdatedAggregateRoots((MQTTResponse) object);
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                jMomBus.post(new RepoSyncedEvent(updatedAggregateRoots));
            }
        }
    }

    private void respond(MQTTRequest mqttRequest) {
        try {
            Set<AggregateRootResponse> aggregateRootResponses = from(mqttRequest.requests)
                        .transform(request -> createResponseFor(request))
                        .toSet();
            mqttClientService.sendMessage(TOPIC_SYNCED_REPO, new MQTTResponse(mqttRequest, aggregateRootResponses));
        } catch (TransportException e) {
            e.printStackTrace();
        }
    }

    private AggregateRootResponse createResponseFor(AggregateRootRequest request) {
        try {
            AggregateRoot loadedAggregateRoot = repo.load(request.name, Repo.FallbackMode.NULL);
            if (loadedAggregateRoot == null) {
                return new AggregateRootResponse(request.name, AggregateRootResponse.Status.NO_CONTENT);
            } else {
                if (loadedAggregateRoot.getVersion() == request.version) {
                    return new AggregateRootResponse(request.name, AggregateRootResponse.Status.NOT_MODIFIED);
                } else {
                    return new AggregateRootResponse(request.name, AggregateRootResponse.Status.OK, objectMapper.writeValueAsBytes(loadedAggregateRoot));
                }
            }
        } catch (RuntimeException e) {
            return new AggregateRootResponse(request.name, AggregateRootResponse.Status.ERROR);
        }
    }

    private Set<String> updateRepoAndReturnUpdatedAggregateRoots(MQTTResponse mqttResponse) {
        Set<String> updatedAggregateRoots = from(mqttResponse.responses)
                .forEachItem(aggregateRootResponse -> updateAggregateRoot(aggregateRootResponse))
                .transform(aggregateRootResponse -> aggregateRootResponse.name)
                .toSet();
        return updatedAggregateRoots;
    }

    private void updateAggregateRoot(AggregateRootResponse aggregateRootResponse) {
        if (AggregateRootResponse.Status.OK.equals(aggregateRootResponse.status)) {
            try {
                Object newAggragrateRoot = objectMapper.readValue(aggregateRootResponse.content, Class.forName(aggregateRootResponse.name));
                repo.save((AggregateRoot) newAggragrateRoot);
            } catch (ClassNotFoundException shouldNotHappen) {
                shouldNotHappen.printStackTrace();
            } catch (IOException shouldNotHappen) {
                shouldNotHappen.printStackTrace();
            }
        }
    }

    private void sendEmptyRepoSyncedEventIfNoResponseAfter10Seconds() {
        countDownLatch = new CountDownLatch(1);

        Runnable runnable = () -> {
            try {
                countDownLatch.await(10, TimeUnit.SECONDS);
                if (countDownLatch.getCount() > 0) {
                    jMomBus.post(new RepoSyncedEvent());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        new Thread(runnable).start();
    }

    private static class MQTTRequest extends Message {
        private Set<AggregateRootRequest> requests;

        private MQTTRequest() {
            super(null);
        }

        public MQTTRequest(Set<AggregateRootRequest> requests) {
            super(UUID.randomUUID());
            this.requests = requests;
        }

    }

    private static class AggregateRootRequest {

        private String name;
        private long version;

        private AggregateRootRequest() {
        }

        public AggregateRootRequest(String name, long version) {
            this.name = name;
            this.version = version;
        }
    }

    private static class MQTTResponse extends Message {
        private Set<AggregateRootResponse> responses;

        private MQTTResponse() {
            super(null);
        }

        public MQTTResponse(MQTTRequest request, Set<AggregateRootResponse> responses) {
            super(request.getCorrelationId());
            this.responses = responses;
        }

    }

    private static class AggregateRootResponse {

        private enum Status {
            NOT_MODIFIED, OK, NO_CONTENT, ERROR
        }

        private String name;
        private Status status;
        private byte[] content;

        private AggregateRootResponse() {
        }

        public AggregateRootResponse(String name, Status status) {
            this.name = name;
            this.status = status;
        }

        public AggregateRootResponse(String name, Status status, byte[] content) {
            this.name = name;
            this.status = status;
            this.content = content;
        }
    }
}
