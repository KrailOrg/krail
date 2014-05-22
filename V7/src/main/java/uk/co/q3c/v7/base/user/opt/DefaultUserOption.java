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
package uk.co.q3c.v7.base.user.opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A set of user options. Although not mandatory, typically the option group and option are the simple class name and
 * field of the object (respectively) requiring the option value. <br>
 * <br>
 * The {@link UserOptionStore} is injected to enable use of different storage methods.
 *
 * @author David Sowerby 15 Jul 2013
 *
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
	public void setOption(String optionGroup, String option, boolean value) {
		userOptionStore.optionMap(optionGroup, option).put(option, Boolean.toString(value));
	}

	public void setOption(String optionGroup, String option, List<String> list) {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		for (String s : list) {
			if (!first) {
				buf.append("|");
			} else {
				first = false;
			}
			buf.append(s);
		}

		userOptionStore.optionMap(optionGroup, option).put(option, buf.toString());
	}

	public void setOption(String optionGroup, String option, Map<String, String> map) {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (!first) {
				buf.append("|");
			} else {
				first = false;
			}
			buf.append(entry.getKey());
			buf.append("=");
			buf.append(entry.getValue());
		}
		userOptionStore.optionMap(optionGroup, option).put(option, buf.toString());
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

	@Override
	public boolean getOptionAsBoolean(String optionGroup, String option, boolean defaultValue) {
		String optionValue = userOptionStore.getOptionValue(optionGroup, option);
		if (optionValue == null) {
			return defaultValue;
		} else {
			try {
				return Boolean.parseBoolean(optionValue);
			} catch (Exception e) {
				log.warn("Invalid option value {} for " + optionGroup + "." + option, optionValue);
				return defaultValue;
			}
		}
	}

	public Map<String, String> getOptionAsMap(String optionGroup, String option, Map<String, String> defaultValue) {
		String optionValue = userOptionStore.getOptionValue(optionGroup, option);
		if (optionValue == null) {
			return defaultValue;
		} else {
			Map<String, String> map = new TreeMap<>();
			Iterable<String> entries = Splitter.on("|").split(optionValue);
			for (String entry : entries) {
				String[] kv = entry.split("=");
				map.put(kv[0], kv[1]);
			}
			return map;
		}
	}

	public List<String> getOptionAsList(String optionGroup, String option, List<String> defaultValue) {
		String optionValue = userOptionStore.getOptionValue(optionGroup, option);
		if (optionValue == null) {
			return defaultValue;
		} else {
			Iterable<String> entries = Splitter.on("|").split(optionValue);
			ArrayList<String> list = Lists.newArrayList(entries);
			return list;
		}
	}

	@Override
	public void setOption(String optionGroup, UserOptionProperty option, int value) {
		setOption(optionGroup, option.name(), value);
	}

	@Override
	public void setOption(String optionGroup, UserOptionProperty option, String value) {
		setOption(optionGroup, option.name(), value);
	}

	@Override
	public void setOption(String optionGroup, UserOptionProperty option, DateTime value) {
		setOption(optionGroup, option.name(), value);
	}

	@Override
	public void setOption(String optionGroup, UserOptionProperty option, double value) {
		setOption(optionGroup, option.name(), value);
	}

	@Override
	public void setOption(String optionGroup, UserOptionProperty option, boolean value) {
		setOption(optionGroup, option.name(), value);
	}

	@Override
	public int getOptionAsInt(String optionGroup, UserOptionProperty option, int defaultValue) {
		return getOptionAsInt(optionGroup, option.name(), defaultValue);
	}

	@Override
	public String getOptionAsString(String optionGroup, UserOptionProperty option, String defaultValue) {
		return getOptionAsString(optionGroup, option.name(), defaultValue);
	}

	@Override
	public DateTime getOptionAsDateTime(String optionGroup, UserOptionProperty option, DateTime defaultValue) {
		return getOptionAsDateTime(optionGroup, option.name(), defaultValue);
	}

	@Override
	public double getOptionAsDouble(String optionGroup, UserOptionProperty option, double defaultValue) {
		return getOptionAsDouble(optionGroup, option.name(), defaultValue);
	}

	@Override
	public boolean getOptionAsBoolean(String optionGroup, UserOptionProperty option, boolean defaultValue) {
		return getOptionAsBoolean(optionGroup, option.name(), defaultValue);
	}

}
