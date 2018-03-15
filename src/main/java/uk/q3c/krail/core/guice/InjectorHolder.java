package uk.q3c.krail.core.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by David Sowerby on 14 Mar 2018
 */
public class InjectorHolder {
    private static Logger log = LoggerFactory.getLogger(InjectorHolder.class);

    private static Injector injector;
    private static BindingsCollator bindingsCollator;

    private static void createInjector() {
        injector = Guice.createInjector(bindingsCollator.allModules());
        log.debug("injector created");
    }

    public static Injector getInjector() {
        if (injector == null) {
            createInjector();
        }
        return injector;
    }

    public static void setBindingsCollator(BindingsCollator bindingsCollator) {
        InjectorHolder.bindingsCollator = bindingsCollator;
    }
}
