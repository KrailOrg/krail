package uk.q3c.krail.core.user.opt;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import org.apache.shiro.subject.Subject;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;

import java.util.List;
import java.util.Optional;

/**
 * Created by David Sowerby on 03/12/14.
 */
public class DefaultUserOption implements UserOption {

    private final String systemLayer = "99:system";
    private final UserOptionLayerDefinition layerDefinition;
    private final SubjectProvider subjectProvider;
    private Class<? extends UserOptionContext> consumerClass;
    //This might be useful for pre-loading a set of options, but is not implemented yet
    private Class<? extends Enum> keys;
    private UserOptionStore optionStore;
    private SubjectIdentifier subjectIdentifier;

    @Inject
    protected DefaultUserOption(UserOptionStore optionStore, UserOptionLayerDefinition layerDefinition,
                                SubjectProvider subjectProvider, SubjectIdentifier subjectIdentifier) {
        this.optionStore = optionStore;
        this.layerDefinition = layerDefinition;
        this.subjectProvider = subjectProvider;
        this.subjectIdentifier = subjectIdentifier;
    }

    @Override
    public void configure(UserOptionContext consumer, Class<? extends Enum> keys) {
        this.consumerClass = consumer.getClass();
        this.keys = keys;
    }

    @Override
    public void configure(Class<? extends UserOptionContext> consumerClass, Class<? extends Enum> keys) {
        this.consumerClass = consumerClass;
        this.keys = keys;
    }

    /**
     * Gets option value for the {@code key} and {@code qualifiers}, combined with the {@link #consumerClass} provided
     * by
     * the
     * {@link #configure(UserOptionContext, Class)} method
     *
     * @param defaultValue
     *         the default value to be returned if no value is found in the store.  Also determines the type of the
     *         return value
     * @param key
     *         an enum key for the option
     * @param qualifiers
     * @param <T>
     *
     * @return
     */
    @Override
    public <T> T get(T defaultValue, Enum<?> key, String... qualifiers) {
        Joiner joiner = Joiner.on(", ")
                              .skipNulls();
        String joinedQualifiers = joiner.join(qualifiers);
        Subject subject = subjectProvider.get();
        String layerId;

        Optional<T> value;
        if (subject.isAuthenticated()) {
            //try the user level first
            layerId = subjectIdentifier.userId();
            value = optionStore.load(defaultValue, layerId, consumerClass.getClass()
                                                                         .getName(), key.name(), joinedQualifiers);
            if (value.isPresent()) {
                return value.get();
            }
            //no value at user level, so
            //iterate through other levels, returning a value if found
            List<String> layers = layerDefinition.getLayers(subjectIdentifier.userId(), Optional.empty());
            for (String layer : layers) {
                value = optionStore.load(defaultValue, layerId, consumerClass.getClass()
                                                                             .getName(), key.name(), joinedQualifiers);
                if (value.isPresent()) {
                    return value.get();
                }
            }

            //still no value, try the system level
            if (value.isPresent()) {
                return value.get();
            }
        }

        //no value found for user related layers, or user not authenticated, try the system level
        value = optionStore.load(defaultValue, systemLayer, consumerClass.getClass()
                                                                         .getName(), key.name(), joinedQualifiers);

        if (value.isPresent()) {
            return value.get();
        }

        //still nothing found, return the default value
        return defaultValue;
    }

    @Override
    public <T> void set(T value, Enum<?> key, String... qualifiers) {

    }

    @Override
    public void flushCache() {
        optionStore.flushCache();
    }
}
