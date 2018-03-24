package uk.q3c.krail.core.guice;

import com.google.inject.Injector;

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
            new InjectorFactory().createInjector(RuntimeEnvironment.SERVLET);
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
