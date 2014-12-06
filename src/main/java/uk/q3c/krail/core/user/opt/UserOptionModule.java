package uk.q3c.krail.core.user.opt;

import com.google.inject.AbstractModule;

/**
 * Created by David Sowerby on 16/11/14.
 */
public class UserOptionModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bindUserOption();
        bindUserOptionStore();
        bindUserOptionLayerDefinition();
    }
    /**
     * Override this method to provide your own {@link UserOptionLayerDefinition} implementation.
     */
    protected void bindUserOptionLayerDefinition() {
        bind(UserOptionLayerDefinition.class).to(DefaultUserOptionLayerDefinition.class);
    }

    /**
     * Override this method to provide your own {@link UserOption} implementation. If all you want to do is change the
     * storage method, override {@link #bindUserOptionStore()} instead
     */
    protected void bindUserOption() {
        bind(UserOption.class).to(DefaultUserOption.class);
    }

    /**
     * Override this method to provide your own store implementation for user options. This is in effect a DAO
     * implementation
     */
    protected void bindUserOptionStore() {
        bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
    }
}
