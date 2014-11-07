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
package uk.q3c.krail.base.user.opt;

import org.joda.time.DateTime;

/**
 * A set of user options. Although not mandatory, typically the option group and option are the simple class name and
 * field of the object (respectively) requiring the option value. <br>
 * <br>
 * The {@link UserOptionStore} into the constructor of the implementation of this interface to enable use of different
 * storage methods. See {@link DefaultUserOption} for an example <br>
 * <br>
 * all the get methods follow the same principle - a default value is supplied by the caller and returned if there is
 * no
 * value for the required option in the store. This means that the default value is set by the caller from a point
 * close
 * to where that value is used.<br>
 * <br>
 * There is both a String and enum option to specify the option property - enums are better for type safety, but there
 * may also be a need to generated the property name dynamically
 *
 * @author David Sowerby 17 Jul 2013
 */
public interface UserOption {

    public void setOption(String optionGroup, String option, Object value);

    public void setOption(String optionGroup, UserOptionProperty option, Object value);

    public int getOptionAsInt(String optionGroup, String option, int defaultValue);

    public String getOptionAsString(String optionGroup, String option, String defaultValue);

    public DateTime getOptionAsDateTime(String optionGroup, String option, DateTime defaultValue);

    public double getOptionAsDouble(String optionGroup, String option, double defaultValue);

    public boolean getOptionAsBoolean(String optionGroup, String option, boolean defaultValue);

    public Enum<?> getOptionAsEnum(String optionGroup, String option, Enum<?> defaultValue);

    public int getOptionAsInt(String optionGroup, UserOptionProperty option, int defaultValue);

    public String getOptionAsString(String optionGroup, UserOptionProperty option, String defaultValue);

    public DateTime getOptionAsDateTime(String optionGroup, UserOptionProperty option, DateTime defaultValue);

    public double getOptionAsDouble(String optionGroup, UserOptionProperty option, double defaultValue);

    public boolean getOptionAsBoolean(String optionGroup, UserOptionProperty option, boolean defaultValue);

    public Enum<?> getOptionAsEnum(String optionGroup, UserOptionProperty option, Enum<?> defaultValue);

    public void clear();

}
