package uk.q3c.krail.core.config;

import com.google.inject.Inject;
import uk.q3c.krail.config.PathLocator;
import uk.q3c.krail.util.ResourceUtils;

import java.io.File;

/**
 * Created by David Sowerby on 22 Aug 2017
 */
public class KrailPathLocator implements PathLocator {

    private ResourceUtils resourceUtils;

    @Inject
    protected KrailPathLocator(ResourceUtils resourceUtils) {
        this.resourceUtils = resourceUtils;
    }

    @Override
    public File configurationDirectory() {
        return resourceUtils.configurationDirectory();
    }

    @Override
    public File applicationDirectory() {
        return resourceUtils.applicationBaseDirectory();
    }
}
