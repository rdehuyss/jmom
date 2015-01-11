package org.jmom.core.model.interfacing;

import java.util.Map;

public interface Configuration {

    public <T> T get(String key);

    public boolean contains(String key);

}
