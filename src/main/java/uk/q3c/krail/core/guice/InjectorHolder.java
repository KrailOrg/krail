package uk.q3c.krail.core.guice;

import com.google.inject.Injector;
import uk.q3c.krail.config.ConfigurationException;

/**
 *
 * Does nothing but keep a static reference to the Guice inject - used only when running in a Servlet environment
 *
 * Created by David Sowerby on 14 Mar 2018
 */
public class InjectorHolder {

    private static Injector injector;

    public static Injector getInjector() {
        if (injector == null) {
            throw new ConfigurationException("Injector should have been created before this method is called");
        }
        return injector;
    }

    public static void setInjector(Injector injector) {
        InjectorHolder.injector = injector;
    }

    public static boolean hasInjector() {
        return injector != null;
    }
}
