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
package uk.co.q3c.v7.base.shiro;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.vaadin.server.VaadinSession;

/**
 * A DI wrapper for {@link SecurityUtils#getSubject()}
 * 
 * @author David Sowerby 15 Jul 2013
 * 
 */
@Singleton
public class SubjectProvider implements Provider<Subject> {
	private static Logger log = LoggerFactory.getLogger(SubjectProvider.class);
	private final VaadinSessionProvider sessionProvider;

	@Inject
	protected SubjectProvider(VaadinSessionProvider sessionProvider) {
		super();
		this.sessionProvider = sessionProvider;
	}

	@Override
	public Subject get() {
		Subject subject = null;
		try {
			VaadinSession session = sessionProvider.get();
			subject = session.getAttribute(Subject.class);
			if (subject == null) {
				log.debug("VaadinSession is valid, but does not have a stored Subject, creating a new Subject");
				subject = new Subject.Builder().buildSubject();
			}
			return subject;

		} catch (IllegalStateException ise) {
			// this may happen in background threads which are not using a session, or during testing
			log.debug("There is no VaadinSession, creating a new Subject");
			subject = new Subject.Builder().buildSubject();
			return subject;

		}

	}
}
