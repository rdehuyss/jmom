package org.jmom.core.model.interfacing;

import com.google.common.util.concurrent.Service;
import org.jmom.core.model.configuration.Configuration;

public interface InterfaceProvider extends Service {

    String name();

    void configure(Configuration configuration);

}
