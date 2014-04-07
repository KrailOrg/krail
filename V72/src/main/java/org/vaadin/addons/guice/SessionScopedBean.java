package org.vaadin.addons.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.SessionScoped;

@SessionScoped
public class SessionScopedBean {
	private static Logger log = LoggerFactory.getLogger(SessionScopedBean.class);
	private String sessionScopedData = "This is a session scoped object";

	@Inject
	protected SessionScopedBean() {
		log.debug("~~~ creating session object ~~~");
	}

	public String getSessionScopedData() {
		return sessionScopedData;
	}

	public void setSessionScopedData(String sessionScopedData) {
		this.sessionScopedData = sessionScopedData;
	}

}
