/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.user.opt;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * A set of user options. Although not mandatory, typically the option group and option are the simple class name and
 * field of the object (respectively) requiring the option value. <br>
 * <br>
 * The {@link UserOptionStore} is injected to enable use of different storage methods.
 *
 * @author David Sowerby 15 Jul 2013
 */
@Singleton
public class DefaultUserOption implements UserOption {

    private static Logger log = LoggerFactory.getLogger(DefaultUserOption.class);
    private final UserOptionStore userOptionStore;

    @Inject
    public DefaultUserOption(UserOptionStore userOptionStore) {
        super();
        this.userOptionStore = userOptionStore;
    }

    @Override
    public Map<String, String> getOptionAsMap(String optionGroup, Enum<?> option, Map<String, String> defaultValue) {
        return getOptionAsMap(optionGroup, option.name(), defaultValue);
    }

    @Override
    public Map<String, String> getOptionAsMap(String optionGroup, String option, Map<String, String> defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (Map<String, String>) optionValue;
    }

    @Override
    public List<String> getOptionAsList(String optionGroup, Enum<?> option, List<String> defaultValue) {
        return getOptionAsList(optionGroup, option.name(), defaultValue);
    }

    @Override
    public List<String> getOptionAsList(String optionGroup, String option, List<String> defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (List<String>) optionValue;
    }

    @Override
    public int getOptionAsInt(String optionGroup, Enum<?> option, int defaultValue) {
        return getOptionAsInt(optionGroup, option.name(), defaultValue);
    }

    @Override
    public int getOptionAsInt(String optionGroup, String option, int defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (int) optionValue;
    }

    @Override
    public String getOptionAsString(String optionGroup, Enum<?> option, String defaultValue) {
        return getOptionAsString(optionGroup, option.name(), defaultValue);
    }

    @Override
    public String getOptionAsString(String optionGroup, String option, String defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (String) optionValue;
    }

    @Override
    public DateTime getOptionAsDateTime(String optionGroup, Enum<?> option, DateTime defaultValue) {
        return getOptionAsDateTime(optionGroup, option.name(), defaultValue);
    }

    @Override
    public DateTime getOptionAsDateTime(String optionGroup, String option, DateTime defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (DateTime) optionValue;
    }

    @Override
    public double getOptionAsDouble(String optionGroup, Enum<?> option, double defaultValue) {
        return getOptionAsDouble(optionGroup, option.name(), defaultValue);
    }

    @Override
    public double getOptionAsDouble(String optionGroup, String option, double defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (double) optionValue;
    }

    @Override
    public boolean getOptionAsBoolean(String optionGroup, Enum<?> option, boolean defaultValue) {
        return getOptionAsBoolean(optionGroup, option.name(), defaultValue);
    }

    @Override
    public boolean getOptionAsBoolean(String optionGroup, String option, boolean defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (boolean) optionValue;
    }

    @Override
    public Enum<?> getOptionAsEnum(String optionGroup, String option, Enum<?> defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option);
        if (optionValue == null) {
            return defaultValue;
        }
        return (Enum<?>) optionValue;
    }

    @Override
    public Enum<?> getOptionAsEnum(String optionGroup, Enum<?> option, Enum<?> defaultValue) {
        Object optionValue = userOptionStore.getOptionValue(optionGroup, option.name());
        if (optionValue == null) {
            return defaultValue;
        }
        return (Enum<?>) optionValue;
    }

    @Override
    public void setOption(String optionGroup, Enum<?> option, Object value) {
        setOption(optionGroup, option.name(), value);
    }

    @Override
    public void setOption(String optionGroup, String option, Object value) {
        userOptionStore.setOptionValue(optionGroup, option, value);
    }


    @Override
    public void clear() {
        userOptionStore.clear();
    }

}
