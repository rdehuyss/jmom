package org.jmom.core.infrastucture.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Throwables;

public class JMomObjectMapper extends ObjectMapper {

    public JMomObjectMapper() {
        super();
        registerModule(new GuavaModule());
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public byte[] writeValueAsBytes(Object value) {
        try {
            return super.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }
}
