package uk.co.q3c.v7.demo.config;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.BaseIni;

@Singleton
public class DemoIni extends BaseIni {
	private static Logger log = LoggerFactory.getLogger(DemoIni.class);

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

	public DemoIni() {
		super();
	}

	@Override
	public void validate() {
		super.validate();
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
	
}
