/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.user.opt;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import org.apache.shiro.subject.Subject;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.util.KrailCodeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Created by David Sowerby on 03/12/14.
 */
public class DefaultOption implements Option {

    private final String systemLayer = "99:system";
    private final OptionLayerDefinition layerDefinition;
    private final SubjectProvider subjectProvider;
    private Class<? extends OptionContext> context;
    //This might be useful for pre-loading a set of options, but is not implemented yet
    private Class<? extends Enum> keys;
    private OptionStore optionStore;
    private SubjectIdentifier subjectIdentifier;

    @Inject
    protected DefaultOption(OptionStore optionStore, OptionLayerDefinition layerDefinition, SubjectProvider
            subjectProvider, SubjectIdentifier subjectIdentifier) {
        this.optionStore = optionStore;
        this.layerDefinition = layerDefinition;
        this.subjectProvider = subjectProvider;
        this.subjectIdentifier = subjectIdentifier;
    }

    @Override
    public void init(@Nonnull OptionContext context, @Nonnull Class<? extends Enum> keys) {
        this.context = context.getClass();
        this.keys = keys;
    }

    @Override
    public void init(@Nonnull Class<? extends OptionContext> context, @Nonnull Class<? extends Enum> keys) {
        this.context = context;
        this.keys = keys;
    }

    /**
     * Gets option value for the {@code key} and optional {@code qualifiers}, combined with the {@link OptionContext}
     * provided by the {@link #init(OptionContext, Class)} method
     *
     * @param defaultValue
     *         the default value to be returned if no value is found in the store.  Also determines the type of the
     *         return value
     * @param key
     *         an enum key for the option
     * @param qualifiers
     *         optional qualifiers to make it possible to distinguish between otherwise identical options
     * @param <T>
     *         the data type of the option
     *
     * @return
     */
    @Override
    public <T> T get(@Nonnull T defaultValue, @Nonnull Enum<?> key, @Nullable String... qualifiers) {
        if (context == null) {
            String msg = getClass().getSimpleName() + " must be initialised.  Typically this is done by calling " +
                    "option.init in the constructor of the class implementing OptionContext, for example:\n\n option" +
                    ".init(this, OptionProperty.class)\n\n where OptionProperty is a locally defined enum;  see " +
                    "LocaleContainer for example";
            throw new KrailCodeException(msg);
        }
        Joiner joiner = Joiner.on(", ")
                              .skipNulls();
        String joinedQualifiers = joiner.join(qualifiers);
        Subject subject = subjectProvider.get();
        String layerId;

        Optional<T> value;
        if (subject.isAuthenticated()) {
            //try the user level first
            layerId = subjectIdentifier.userId();
            value = optionStore.load(defaultValue, layerId, context.getClass()
                                                                   .getName(), key.name(), joinedQualifiers);
            if (value.isPresent()) {
                return value.get();
            }
            //no value at user level, so
            //iterate through other levels, returning a value if found
            List<String> layers = layerDefinition.getLayers(subjectIdentifier.userId(), Optional.empty());
            for (String layer : layers) {
                value = optionStore.load(defaultValue, layerId, context.getClass()
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
        value = optionStore.load(defaultValue, systemLayer, context.getClass()
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
