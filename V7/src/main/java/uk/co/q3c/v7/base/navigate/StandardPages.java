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
package uk.co.q3c.v7.base.navigate;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewModule;

/**
 * Provides the URIs for a number of 'special' pages - theses are pages which
 * perform a particular role and are common to most applications - login, home,
 * landing etc. See each property for a description of their purpose. The
 * {@link V7View} mapping for each of these is still provided by
 * {@link V7ViewModule}, or more likely, a sub-class of it
 * <p>
 * Values are loaded from the 'pages' section of the 'V7.ini' file, which should
 * be at the project root. Default values are provided in case of file read
 * problems.
 * 
 * @author David Sowerby 20 Jan 2013
 * 
 */
@Singleton
public class StandardPages {
	private static Logger log = LoggerFactory.getLogger(StandardPages.class);

	private boolean loaded = false;
	private String filepath = "classpath:V7.ini";

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFilepath() {
		return filepath;
	}

	/**
	 * The home page for non-authenticated users
	 */
	private final String publicHome = "public/home";

	/**
	 * The home page for non-authenticated users
	 */
	private final String secureHome = "secure/home";

	/**
	 * The login page
	 */
	private final String login = "public/login";

	/**
	 * The page to be presented after a user has logged out
	 */
	private final String logout = "public/logout";

	@Inject
	protected StandardPages() {
		super();
	}

	public void load() {
		V7Ini ini = new V7Ini();
		try {
			log.info("Loading from standard page configuration from {} ", filepath);
			ini.loadFromPath(filepath);
			Section section = ini.getSection("pages");
			if (section == null) {
				log.warn("Configuration file {} does not have a [pages] section. No property values can be loaded, using defaults");
			} else {
				loadSection(section);
				loaded = true;
			}
		} catch (ConfigurationException ce) {
			log.warn(
					"Could not load the standard pages config.  Default values are being used for all properties, caused by exception\n {}",
					ce.getMessage());

		}

	}

	private void loadSection(Section section) {
		Class<?> c = this.getClass();
		try {
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				// obtain the property name

				String name = field.getName();
				// ignore the non-properties
				if (!name.equals("log") && !name.equals("loaded") && !name.equals("filepath")) {
					// retrieve the value for this property from the ini section
					String value = section.get(name);
					if (value != null) {
						field.set(this, value);
					} else {
						log.warn(
								"Property {} has no value specified in the V7.ini file.  Using the default value specified in StandardPages instead",
								name);
					}
				}
			}

		} catch (Exception e) {
			log.warn("Failed to load a V7 property", e);
		}

	}

	public boolean isLoaded() {
		return loaded;
	}

	public String getPublicHome() {
		return publicHome;
	}

	public String getSecureHome() {
		return secureHome;
	}

	public String getLogin() {
		return login;
	}

	public String getLogout() {
		return logout;
	}
}
