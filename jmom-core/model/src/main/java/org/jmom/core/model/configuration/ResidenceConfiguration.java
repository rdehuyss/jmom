package org.jmom.core.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.things.Residence;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;

public class ResidenceConfiguration {

    private final Map<CentralControlUnit, CentralControlUnitConfiguration> configuration;

    ResidenceConfiguration(Residence residence) {
        this(newHashMap());
    }

    @JsonCreator
    private ResidenceConfiguration(
            @JsonProperty("configuration") Map<CentralControlUnit, CentralControlUnitConfiguration> configuration) {
        this.configuration = configuration;
    }

    public boolean containsConfiguration(CentralControlUnit centralControlUnit) {
        return configuration.containsKey(centralControlUnit);
    }

    public CentralControlUnitConfiguration getConfiguration(CentralControlUnit centralControlUnit) {
        return configuration.get(centralControlUnit);
    }

    void saveCentralControlUnitConfiguration(CentralControlUnit centralControlUnit, CentralControlUnitConfiguration centralControlUnitConfiguration) {
        configuration.put(centralControlUnit, centralControlUnitConfiguration);
    }

    public Set<CentralControlUnitConfiguration> getConfigurations() {
        return Sets.newHashSet(configuration.values());
    }
}
