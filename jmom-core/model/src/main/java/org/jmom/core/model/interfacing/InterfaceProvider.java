package org.jmom.core.model.interfacing;

import com.google.common.util.concurrent.Service;

public interface InterfaceProvider extends Service {

    String name();

    void configure(Configuration configuration);

}
