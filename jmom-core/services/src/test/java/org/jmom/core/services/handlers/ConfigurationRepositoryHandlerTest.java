package org.jmom.core.services.handlers;

import com.google.common.collect.Multimap;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.configuration.CentralControlUnitConfiguration;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.configuration.InterfaceProviderConfiguration;
import org.jmom.core.model.configuration.ResidenceConfiguration;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.eda.commands.LinkCentralControlUnitToResidenceCommand;
import org.jmom.core.model.eda.events.InterfaceProviderFoundEvent;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;
import org.jmom.core.model.things.Residence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmom.core.model.eda.events.InterfaceProviderFoundEventTestBuilder.anInterfaceProviderFoundEvent;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationRepositoryHandlerTest {

    public static final String AN_INTERFACE_PROVIDER1 = "An Interface Provider 1";
    public static final String AN_INTERFACE_PROVIDER2 = "An Interface Provider 2";

    private ConfigurationRepositoryHandler handler;

    private ConfigurationRepository configurationRepository;

    @Mock
    private Repo repo;

    private JMomBus jMomBus;

    @Mock
    private InterfaceDiscoverer interfaceDiscoverer1;
    @Mock
    private InterfaceDiscoverer interfaceDiscoverer2;

    private Residence residence = new Residence("Home");

    private CentralControlUnit centralControlUnit = new CentralControlUnit("ronald.dehuysser@gmail.com", "testen", "Central Control Unit");
    private InterfaceProviderFoundEvent interfaceProviderFoundEvent1;
    private InterfaceProviderFoundEvent interfaceProviderFoundEvent2;

    @Before
    public void setupConfigurationRepositoryHandler() {
        configurationRepository = new ConfigurationRepository();
        handler = new ConfigurationRepositoryHandler(configurationRepository, repo);
        jMomBus = new JMomBus();
        jMomBus.register(handler);
        when(interfaceDiscoverer1.name()).thenReturn(AN_INTERFACE_PROVIDER1);
        when(interfaceDiscoverer2.name()).thenReturn(AN_INTERFACE_PROVIDER2);

        interfaceProviderFoundEvent1 = anInterfaceProviderFoundEvent()
                .withInterfaceDiscoverer(interfaceDiscoverer1)
                .withConfiguration("key", "value1")
                .withCentralControlUnit(centralControlUnit)
                .build();
        interfaceProviderFoundEvent2 = anInterfaceProviderFoundEvent()
                .withInterfaceDiscoverer(interfaceDiscoverer2)
                .withConfiguration("key", "value2")
                .withCentralControlUnit(centralControlUnit)
                .build();
    }

    @Test
    public void handleInterfaceProviderFoundEvent_ExistingCentralControlUnit() throws IOException {
        //WHEN
        jMomBus.post(interfaceProviderFoundEvent1);
        jMomBus.post(interfaceProviderFoundEvent2);

        //THEN
        Multimap<CentralControlUnit, InterfaceProviderConfiguration> tempConfigMap = (Multimap<CentralControlUnit, InterfaceProviderConfiguration>) getInternalState(configurationRepository, "unknownCentralControlUnitInterfaceProviderConfiguration");
        Collection<InterfaceProviderConfiguration> interfaceProviderConfigurations = tempConfigMap.get(centralControlUnit);
        assertThat(interfaceProviderConfigurations).hasSize(2);
        verify(repo, times(2)).save(configurationRepository);
        reset(repo);

        //GIVEN
        LinkCentralControlUnitToResidenceCommand linkCentralControlUnitToResidenceCommand = new LinkCentralControlUnitToResidenceCommand(centralControlUnit, residence);

        //WHEN
        jMomBus.post(linkCentralControlUnitToResidenceCommand);

        //THEN
        ResidenceConfiguration residenceConfiguration = configurationRepository.getOrCreateResidenceConfiguration(residence);
        assertThat(residenceConfiguration.getConfigurations()).hasSize(1);
        CentralControlUnitConfiguration actualCentralControlUnitConfiguration = residenceConfiguration.getConfigurations().iterator().next();
        assertInterfaceProviderConfiguration(actualCentralControlUnitConfiguration.getOrCreateConfiguration(AN_INTERFACE_PROVIDER1), AN_INTERFACE_PROVIDER1, "key", "value1");
        assertInterfaceProviderConfiguration(actualCentralControlUnitConfiguration.getOrCreateConfiguration(AN_INTERFACE_PROVIDER2), AN_INTERFACE_PROVIDER2, "key", "value2");

        CentralControlUnitConfiguration centralControlUnitConfiguration = configurationRepository.getCentralControlUnitConfiguration(centralControlUnit);
        assertThat(centralControlUnitConfiguration).isNotNull();
        assertInterfaceProviderConfiguration(centralControlUnitConfiguration.getOrCreateConfiguration(AN_INTERFACE_PROVIDER1), AN_INTERFACE_PROVIDER1, "key", "value1");
        assertInterfaceProviderConfiguration(centralControlUnitConfiguration.getOrCreateConfiguration(AN_INTERFACE_PROVIDER2), AN_INTERFACE_PROVIDER2, "key", "value2");
        assertThat(((Multimap) (getInternalState(configurationRepository, "unknownCentralControlUnitInterfaceProviderConfiguration"))).size()).isZero();
        verify(repo).save(configurationRepository);
    }

    @Test
    public void serialization() throws IOException {
        //WHEN
        jMomBus.post(interfaceProviderFoundEvent1);
        jMomBus.post(interfaceProviderFoundEvent2);

        JMomObjectMapper jMomObjectMapper = new JMomObjectMapper();
        String result = jMomObjectMapper.writeValueAsString(configurationRepository);
        System.out.println(result);

        ConfigurationRepository actualConfigurationRepository = jMomObjectMapper.readValue(result, ConfigurationRepository.class);
        System.out.println(actualConfigurationRepository);
    }

    private void assertInterfaceProviderConfiguration(InterfaceProviderConfiguration configuration, String name, String key, String value) {
        assertThat(configuration).isNotNull();
        assertThat(configuration.getInterfaceProviderName()).isEqualTo(name);
        assertThat((String) configuration.get(key)).isEqualTo(value);
    }

}