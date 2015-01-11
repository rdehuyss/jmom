package org.jmom.core.infrastucture.bus;

import com.google.common.base.Predicate;
import org.jmom.core.infrastucture.eda.Message;

public interface JMomBusInterceptor extends Predicate<Message> {

}
