package uk.q3c.krail.core.user.opt;

import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Sowerby on 05/12/14.
 */
public class MockUserOption implements UserOption {


    private Class<? extends UserOptionConsumer> consumerClass;
    private Map<String, Object> map = new HashMap<>();

    @Override
    public void configure(UserOptionConsumer consumer, Class<? extends Enum> keys) {
        this.consumerClass = consumer.getClass();
    }

    @Override
    public void configure(Class<? extends UserOptionConsumer> consumerClass, Class<? extends Enum> keys) {
        this.consumerClass = consumerClass;
    }


    @Override
    public <T> T get(T defaultValue, Enum<?> key, String... qualifiers) {
        T value = (T) map.get(key(key, qualifiers));
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private String key(Enum<?> key, String... qualifiers) {
        Joiner qjoiner = Joiner.on(",")
                               .skipNulls();
        String joinedQualifiers = qjoiner.join(qualifiers);

        Joiner joiner = Joiner.on("-")
                              .skipNulls();
        String fullKey = joiner.join(consumerClass, key.name(), joinedQualifiers);
        return fullKey;
    }

    @Override
    public <T> void set(T value, Enum<?> key, String... qualifiers) {
        map.put(key(key, qualifiers), value);
    }

    /**
     * Flushes the cache of the associated option store, for the current user
     */
    @Override
    public void flushCache() {

    }
}
