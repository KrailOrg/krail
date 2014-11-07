/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.ui;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;
import uk.q3c.krail.base.data.V7DefaultConverterFactory;
import uk.q3c.krail.base.guice.uiscope.UIScoped;
import uk.q3c.krail.base.navigate.DefaultV7Navigator;
import uk.q3c.krail.base.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.base.navigate.URIFragmentHandler;
import uk.q3c.krail.base.navigate.V7Navigator;
import uk.q3c.krail.base.view.component.DefaultUserStatusPanel;
import uk.q3c.krail.base.view.component.UserStatusPanel;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

public abstract class V7UIModule extends AbstractModule {

    @Override
    protected void configure() {
        bindApplicationTitle();
        MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);

        bind(WebBrowser.class).toProvider(BrowserProvider.class);

        bindUIProvider();
        addUIBindings(mapbinder);
        bindNavigator();
        bindURIHandler();
        bindConverterFactory();
        bindLoginStatusMonitor();

    }

    private void bindApplicationTitle() {
        ApplicationTitle title = new ApplicationTitle(applicationTitleKey());
        bind(ApplicationTitle.class).toInstance(title);
    }

    /**
     * override this method to provide the I18Nkey which defines your application title (which appears in your browser
     * tab)
     */
    protected I18NKey<?> applicationTitleKey() {
        return LabelKey.V7;
    }

    private void bindConverterFactory() {
        bind(ConverterFactory.class).to(V7DefaultConverterFactory.class);
    }

    /**
     * Override to bind your choice of LoginStatusMonitor
     */
    protected void bindLoginStatusMonitor() {
        bind(UserStatusPanel.class).to(DefaultUserStatusPanel.class);
    }

    /**
     * Override to bind your ScopedUIProvider implementation
     */
    protected abstract void bindUIProvider();

    /**
     * Override to bind your choice of URI handler
     */
    protected void bindURIHandler() {
        bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
    }

    protected void bindNavigator() {
        bind(V7Navigator.class).to(DefaultV7Navigator.class)
                               .in(UIScoped.class);
    }

    /**
     * Override with your UI bindings
     *
     * @param mapbinder
     */
    protected void addUIBindings(MapBinder<String, UI> mapbinder) {
        mapbinder.addBinding(BasicUI.class.getName())
                 .to(BasicUI.class);
    }

}
