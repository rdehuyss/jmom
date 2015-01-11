package org.jmom.core.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jmom.core.model.interfacing.Configuration;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class InterfaceProviderConfiguration implements Configuration {

    private final String interfaceProviderName;
    private final Map<String, Object> configuration;

    InterfaceProviderConfiguration(String interfaceProviderName) {
        this(interfaceProviderName, newHashMap());
    }

    @JsonCreator
    InterfaceProviderConfiguration(
            @JsonProperty("interfaceProviderName") String interfaceProviderName,
            @JsonProperty("configuration") Map<String, Object> configuration) {
        this.interfaceProviderName = interfaceProviderName;
        this.configuration = configuration;
    }


    public String getInterfaceProviderName() {
        return interfaceProviderName;
    }

    @Override
    public <T> T get(String key) {
        return (T) configuration.get(key);
    }

    @Override
    public boolean contains(String key) {
        return configuration.containsKey(key);
    }

    void putAll(Map<String, Object> configuration) {
        this.configuration.putAll(configuration);
    }
}
