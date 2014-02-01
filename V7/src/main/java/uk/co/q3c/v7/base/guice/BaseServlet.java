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
package uk.co.q3c.v7.base.guice;

import java.util.Properties;

import uk.co.q3c.v7.base.ui.V7UIModule;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;

@Singleton
public class BaseServlet extends VaadinServlet implements SessionInitListener {

	/**
	 * Cannot use constructor injection. Container expects servlet to have no-arg public constructor
	 */
	@Inject
	private UIProvider basicProvider;

	@Inject
	@V7Widgetset
	private String widgetset;

	@Override
	protected void servletInitialized() {
		getService().addSessionInitListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		event.getSession().addUIProvider(basicProvider);
	}

	/**
	 * This method captures the widgetset parameter from the application specific subclass of {@link V7UIModule}. If
	 * V7UIModule#widgetset() is unchanged (that is, it returns 'default') then the default widgetset is used. For any
	 * other value, the related widgetset must have been compiled - typically this means that the build definition will
	 * also contain an entry for the widgetset. For example, using the Gradle Vaadin plugin, the build.gradle file would
	 * contain an entry like this:<br>
	 * <p>
	 * 
	 * <pre>
	 * vaadin {<br>
	 * version vaadinVersion<br>
	 * widgetset "uk.co.q3c.v7.demo.widgetset.V7demoWidgetset"<br>
	 * }
	 * </pre>
	 * <p>
	 * 
	 * @see https://github.com/johndevs/gradle-vaadin-plugin
	 * @see com.vaadin.server.VaadinServlet#createDeploymentConfiguration(java.util.Properties)
	 */
	@Override
	protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {

		if (!widgetset.equals("default")) {
			initParameters.setProperty("widgetset", widgetset);
		}
		return super.createDeploymentConfiguration(initParameters);

	}
}