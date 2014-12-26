package org.jmom.core.infrastucture.bus;

import com.google.common.base.Predicate;

public interface JMomBusFilter<T extends Object> extends Predicate<T> {

}
