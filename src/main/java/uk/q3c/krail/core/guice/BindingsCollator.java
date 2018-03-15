package uk.q3c.krail.core.guice;

import com.google.inject.Module;

import java.util.List;

/**
 * Created by David Sowerby on 14 Mar 2018
 */
public interface BindingsCollator {
    List<Module> allModules();
}
