package org.vaadin.addons.guice.server;

import org.vaadin.addons.guice.servlet.VGuiceApplicationServlet;

import com.google.inject.servlet.ServletModule;

/**
 * 
 * @author Will Temperley
 *
 */
public class ExampleGuiceServletModule extends ServletModule {

    @Override
    protected void configureServlets() {

        serve("/*").with(VGuiceApplicationServlet.class);

    }


}