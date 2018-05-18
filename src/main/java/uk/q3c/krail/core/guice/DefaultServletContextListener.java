/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.guice;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.env.InjectorFactory;
import uk.q3c.krail.core.env.RuntimeEnvironment;
import uk.q3c.krail.service.ServiceMonitor;

import javax.servlet.ServletContextEvent;


/**
 * As this is created by the web container, a parameterless constructor is needed.
 * <p>
 * The Guice injector is held separately, in {@link InjectorHolder} to enable access for a war file deployment and a Vertx deployment.
 */
public class DefaultServletContextListener extends GuiceServletContextListener {
    private static Logger log = LoggerFactory.getLogger(DefaultServletContextListener.class);

    protected DefaultServletContextListener() {
        super();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        log.debug("Servlet context listener initialised");
//        ApplicationStartup startup = InjectorHolder.getInjector().getInstance(ApplicationStartup.class);
//        startup.invoke();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("Stopping service");
        try {
            if (InjectorHolder.hasInjector()) {
                InjectorHolder.getInjector().getInstance(ServiceMonitor.class)
                        .stopAllServices();
            } else {
                log.debug("Injector has not been constructed, no call made to stop service");
            }
        } catch (Exception e) {
            log.error("Exception while stopping service", e);
        }
        //context may not have been created, and super does not check for it
        if (servletContextEvent.getServletContext() != null) {
            super.contextDestroyed(servletContextEvent);
        }
    }

    /**
     * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
     */
    @Override
    public Injector getInjector() {
        if (!InjectorHolder.hasInjector()) {
            InjectorFactory factory = new InjectorFactory();
            // this creates the injector and puts it in InjectorHolder via InjectorLocator
            factory.createInjector(RuntimeEnvironment.SERVLET);
        }
        return InjectorHolder.getInjector();
    }


}