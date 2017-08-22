package uk.q3c.krail.core.config;

import uk.q3c.krail.config.PathLocator;
import uk.q3c.krail.config.bind.ApplicationConfigurationModule;

/**
 * Created by David Sowerby on 22 Aug 2017
 */
public class KrailApplicationConfigurationModule extends ApplicationConfigurationModule {

    @Override
    protected void configure() {
        super.configure();
        bindPathLocator();
    }


    protected void bindPathLocator() {
        bind(PathLocator.class).to(KrailPathLocator.class);
    }
}
