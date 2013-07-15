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
package uk.co.q3c.v7.base.useropt;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of user options. Although not mandatory, typically the option group and option are the simple class name and
 * field of the object (respectively) requiring the option value. <br>
 * <br>
 * The storage mechanism is injected to enable use of different storage methods.
 * 
 * @author David Sowerby 15 Jul 2013
 * 
 */
@Singleton
public class DefaultUserOption implements UserOption {

	private static Logger log = LoggerFactory.getLogger(DefaultUserOption.class);
	private final UserOptionStore userOptionStore;

	@Inject
	protected DefaultUserOption(UserOptionStore userOptionStore) {
		super();
		this.userOptionStore = userOptionStore;
	}

	@Override
	public void setOption(String optionGroup, String option, int value) {
		userOptionStore.optionMap(optionGroup, option).put(option, Integer.toString(value));
	}

	@Override
	public void setOption(String optionGroup, String option, String value) {
		userOptionStore.optionMap(optionGroup, option).put(option, value);
	}

	@Override
	public void setOption(String optionGroup, String option, DateTime value) {
		userOptionStore.optionMap(optionGroup, option).put(option, value.toString());
	}

	@Override
	public void setOption(String optionGroup, String option, double value) {
		userOptionStore.optionMap(optionGroup, option).put(option, Double.toString(value));
	}

	@Override
	public int getOptionAsInt(String optionGroup, String option, int defaultValue) {
		String optionValue = userOptionStore.getOptionValue(optionGroup, option);
		if (optionValue == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(optionValue);
			} catch (Exception e) {
				log.warn("Invalid option value {} for " + optionGroup + "." + option, optionValue);
				return defaultValue;
			}
		}
	}

	@Override
	public String getOptionAsString(String optionGroup, String option, String defaultValue) {
		String optionValue = userOptionStore.getOptionValue(optionGroup, option);
		if (optionValue == null) {
			return defaultValue;
		}
		return optionValue;
	}

	@Override
	public DateTime getOptionAsDateTime(String optionGroup, String option, DateTime defaultValue) {
		String optionValue = userOptionStore.getOptionValue(optionGroup, option);
		if (optionValue == null) {
			return defaultValue;
		} else {
			try {
				return DateTime.parse(optionValue);
			} catch (Exception e) {
				log.warn("Invalid option value {} for " + optionGroup + "." + option, optionValue);
				return defaultValue;
			}
		}
	}

	@Override
	public double getOptionAsDouble(String optionGroup, String option, double defaultValue) {
		String optionValue = userOptionStore.getOptionValue(optionGroup, option);
		if (optionValue == null) {
			return defaultValue;
		} else {
			try {
				return Double.parseDouble(optionValue);
			} catch (Exception e) {
				log.warn("Invalid option value {} for " + optionGroup + "." + option, optionValue);
				return defaultValue;
			}
		}
	}

}
