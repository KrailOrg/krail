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
package uk.q3c.krail.core.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vaadin.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.ui.ScopedUIProvider;

import java.util.Properties;

@Singleton
public class BaseServlet extends VaadinServlet implements SessionInitListener {
    private static Logger log = LoggerFactory.getLogger(BaseServlet.class);

    private final ScopedUIProvider uiProvider;

    @Inject
    public BaseServlet(ScopedUIProvider uiProvider) {
        this.uiProvider = uiProvider;
    }

    @Override
    protected void servletInitialized() {
        getService().addSessionInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        event.getSession()
             .addUIProvider(uiProvider);
    }

    /**
     * This method captures the parameters from appropriate methods and sets the servlet parameters accordingly.
     * <p>
     * <pre>
     * vaadin {<br>
     * version vaadinVersion<br>
     * widgetset "uk.q3c.krail.demo.widgetset.demoWidgetset"<br>
     * }
     * </pre>
     * <p>
     * @see <a href="https://github.com/johndevs/gradle-vaadin-plugin"> https://github.com/johndevs/gradle-vaadin-plugin</a>
     * @see com.vaadin.server.VaadinServlet#createDeploymentConfiguration(java.util.Properties)
     */
    @Override
    protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
        log.debug("creating deployment configuration");

        if (!widgetset().equals("default")) {
            log.debug("Setting widgetset parameter to '{}'", widgetset());
            initParameters.setProperty("widgetset", widgetset());
        } else {
            log.debug("Using default widgetset");
        }
        initParameters.setProperty("productionMode", Boolean.toString(productionMode()));
        return super.createDeploymentConfiguration(initParameters);

    }

    /**
     * Returns the widgetset parameter for this servlet. If it is unchanged (that is, it returns 'default') then the
     * default widgetset is used. For any other value (as defined by a sub-class implementation), the related widgetset
     * must have been compiled - typically this means that the build definition will also contain an entry for the
     * widgetset. For example, using the Gradle Vaadin plugin, the build.gradle file would contain an entry like
     * this:<br><br>
     * vaadin {<br><br>
     * widgetset "uk.q3c.krail.demo.widgetset.demoWidgetset"<br>
     * <p>
     * }<br>
     * <p>
     *
     * @return the String defining the widgetset to use
     */
    protected String widgetset() {
        return "default";
    }

    protected boolean productionMode() {
        return false;
    }

}