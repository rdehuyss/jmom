package org.jmom.core.services.remoting.client;

import com.google.common.collect.Sets;
import org.jmom.core.infrastucture.eda.Event;
import org.jmom.core.infrastucture.eda.LocalMessage;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class RepoSyncedEvent extends Event implements LocalMessage {

    private Set<String> aggregateRootsThatAreSynced;

    public RepoSyncedEvent() {
        aggregateRootsThatAreSynced = newHashSet();
    }

    public RepoSyncedEvent(Set<String> aggregateRootsThatAreSynced) {
        this.aggregateRootsThatAreSynced = aggregateRootsThatAreSynced;
    }

    public boolean isDataSynced() {
        return aggregateRootsThatAreSynced.size() > 0;
    }

    public Set<String> getAggregateRootsThatAreSynced() {
        return aggregateRootsThatAreSynced;
    }
}
