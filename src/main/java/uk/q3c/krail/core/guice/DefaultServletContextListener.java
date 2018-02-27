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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.service.ServiceModel;

import javax.servlet.ServletContextEvent;


/**
 * A subclass of {@link BindingsCollator} is used to collect Guice modules -  this separates the definitions of modules to use from the {@link Injector}  creation.
 * <p>
 * This allows the same {@link BindingsCollator} to be used for a war file deployment and a Vertx deployment.
 * <p>
 * As this is created by the web container, a parameterless constructor is needed.  Create a sub-class and return your
 * implementation of {@link BindingsCollator} to provide a definition of the Guice modules to include
 */
public abstract class DefaultServletContextListener extends GuiceServletContextListener {
    //Visible for testing
    static Injector injector;
    private static Logger log = LoggerFactory.getLogger(DefaultServletContextListener.class);

    protected DefaultServletContextListener() {
        super();
    }

    public static Injector injector() {
        return injector;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("Stopping service");
        try {
            if (injector != null) {
                injector.getInstance(ServiceModel.class)
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
        if (injector == null) {
            createInjector();
        }
        return injector;
    }

    protected void createInjector() {
        injector = Guice.createInjector(getBindingsCollator().allModules());
        log.debug("injector created");

        // By default Shiro provides a binding to DefaultSecurityManager, but that is replaced by a binding to
        // KrailSecurityManager in {@link DefaultShiroModule#bindSecurityManager} (or potentially to another security manager if
        // the developer overrides that method)
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        SecurityUtils.setSecurityManager(securityManager);

    }

    protected abstract BindingsCollator getBindingsCollator();


}