package uk.co.q3c.v7.base.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;

import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the Shiro {@link Ini} class validate that all required entries are defined. Missing entries will either have
 * defaults set or an exception raised if appropriate. <b>NOTE:</b>Only {@link #loadFromPath(String)} has been
 * overloaded; if you need to use any of the other load methods from {@link Ini}, you will need to call
 * {@link #validate()} after the load to provide defaults.
 * 
 */
@Singleton
public class V7Ini extends Ini {
	private static Logger log = LoggerFactory.getLogger(V7Ini.class);
	private static final String defaultPath = "classpath:V7.ini";

	public static enum DbParam {
		dbURL,
		dbUser,
		dbPwd
	}

	private static String dbDefault(DbParam key) {
		switch (key) {
		case dbURL:
			return "memory:scratchpad";
		case dbUser:
			return "admin";
		case dbPwd:
			return "admin";
		default:
			return "unknown";
		}

	}

	public V7Ini() {
		super();
	}

	public void validate() {
		// validatePages(checkSection("pages"));
		validateDb(checkSection("db"));
	}

	private void validateDb(Section section) {
		for (DbParam key : DbParam.values()) {
			if (!section.containsKey(key.name())) {
				log.warn("The property {} is missing from V7.ini, using the default value", key.name());
				section.put(key.name(), dbDefault(key));
			}
		}
	}

	private Section checkSection(String sectionName) {
		Section section = getSection(sectionName);
		if (section == null) {
			log.warn("The section {} is missing from V7.ini, using default values", sectionName);
			section = this.addSection(sectionName);
		}
		return section;
	}

	// protected void validatePages(Section section) {
	// for (StandardPageKey pageKey : StandardPageKey.values()) {
	// if (!section.containsKey(pageKey.name())) {
	// log.warn("The property {} is missing from V7.ini, using the default value", pageKey.name());
	// section.put(pageKey.name(), pageDefault(pageKey));
	// }
	// }
	// }

	@Override
	public void loadFromPath(String resourcePath) throws ConfigurationException {
		try {
			super.loadFromPath(resourcePath);
		} catch (ConfigurationException ce) {
			log.warn("Unable to load V7.ini from {}, using defaults.", resourcePath);
		} finally {
			validate();
		}
	}

	/**
	 * Calls {@link #loadFromPath(String)} with the {@link #defaultPath}
	 */
	public void load() {
		loadFromPath(defaultPath);
	}

	// public String standardPageURI(StandardPageKey pageKey) {
	// Section section = getSection("pages");
	// if (section == null) {
	// throw new ConfigurationException(
	// "The 'pages' section of V7Ini is null, which should not be possible.  Are you testing and have forgotten to call V7Ini.validate() in your @Before method?  Yopu may be able to resolve this by including IniModule in your @GuiceCOntext (see BasicUITest for an example)");
	// }
	// String path = section.get(pageKey.name());
	// return path;
	//
	// }

	public String dbParam(DbParam dbParam) {
		Section section = getSection("db");
		if (section == null) {
			log.warn("db section should not be null in V7ini");
			return null;
		} else {
			String value = section.get(dbParam.name());
			return value;
		}
	}

	public void save(String base, String directory, String filename) {
		File f = ConfigUtil.fileFromPathWithVariable(base, directory, filename);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			for (Section section : getSections()) {
				bw.write("[" + section.getName() + "]\n");
				for (Map.Entry<String, String> entry : section.entrySet()) {
					bw.write(entry.getKey() + "=" + entry.getValue() + "\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			log.error("error saving to ini file", e);
		}
		log.info("Ini file saved: " + f.getAbsolutePath());
	}

	public boolean optionReadSiteMap() {
		return getBooleanOption("readSiteMap");
	}

	private boolean getBooleanOption(String optionName) {
		Section section = getSection("options");
		if (section == null) {
			return booleanOptionDefault(optionName);
		}
		if (section.containsKey(optionName)) {
			return section.get(optionName).equals("true");
		} else {
			return booleanOptionDefault(optionName);
		}
	}

	private boolean booleanOptionDefault(String optionName) {
		switch (optionName) {
		case "readSiteMap":
			return true;
		}
		return false;
	}

}
