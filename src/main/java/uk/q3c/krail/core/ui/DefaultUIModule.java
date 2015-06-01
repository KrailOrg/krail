/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.ui;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.server.WebBrowser;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

import javax.annotation.Nonnull;

public class DefaultUIModule extends AbstractModule {

    private I18NKey applicationTitleKey;
    private Class<? extends ScopedUI> uiClass;

    private MapBinder<String, Class<? extends ScopedUI>> uiMapBinder;

    public DefaultUIModule() {
        uiClass = DefaultApplicationUI.class;
        applicationTitleKey = LabelKey.Krail;
    }

    @Override
    protected void configure() {
        TypeLiteral<String> annotationTypeLiteral = new TypeLiteral<String>() {
        };

        TypeLiteral<Class<? extends ScopedUI>> scopedUIClassLiteral = new TypeLiteral<Class<? extends ScopedUI>>() {
        };

        uiMapBinder = MapBinder.newMapBinder(binder(), annotationTypeLiteral, scopedUIClassLiteral);


        bindApplicationTitle();
        TypeLiteral<Class<? extends ScopedUI>> scopedUiclass = new TypeLiteral<Class<? extends ScopedUI>>() {
        };
        TypeLiteral<String> stringClass = new TypeLiteral<String>() {
        };


        bind(WebBrowser.class).toProvider(BrowserProvider.class);
        bindUIProvider();
        define();


    }

    /**
     * Override this method to bind your own UI class(es). If you will only be using a single UI class, it is easier to call {@link #uiClass(Class)}, which you
     * can do from your Binding Manager. If you wish to use more than one UI class, you will also need to provide a custom {@link
     * ScopedUIProvider}, and bind it by overriding {@link #bindUIProvider()}
     */
    protected void define() {
        addUIBinding(uiClass);
    }

    protected void addUIBinding(Class<? extends ScopedUI> uIClass) {
        uiMapBinder.addBinding(uIClass.getName())
                   .toInstance(uIClass);
    }

    private void bindApplicationTitle() {
        ApplicationTitle title = new ApplicationTitle(applicationTitleKey);
        bind(ApplicationTitle.class).toInstance(title);
    }


    /**
     * Override to bind your ScopedUIProvider implementation
     */
    protected void bindUIProvider() {
        bind(ScopedUIProvider.class);
    }

    /**
     * Sets a single UI class.  If you need multiple UI classes override {@link #define()} and refer to the javadoc for that method.  Typically this method is
     * called by:<br><br> new DefaultUIModule().uiClass(aClass)<br><br>
     *
     * @param uiClass
     *         the UI class to use for the whole application
     *
     * @return this, for fluency
     */
    public DefaultUIModule uiClass(Class<? extends ScopedUI> uiClass) {
        this.uiClass = uiClass;
        return this;
    }

    public DefaultUIModule applicationTitleKey(@Nonnull I18NKey applicationTitleKey) {
        this.applicationTitleKey = applicationTitleKey;
        return this;
    }

}
