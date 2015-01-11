package org.jmom.core.infrastucture;

public interface BeanProvider {

    <T> T getBean(Class<T> clazz);

}
