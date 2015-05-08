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

public class DefaultUIModule extends AbstractModule {

    private MapBinder<String, Class<? extends ScopedUI>> uiMapBinder;

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
     * Override this method to bind your own UI class(es).  If you wish to use more than one UI class, you will also need to provide a custom {@link
     * ScopedUIProvider}, and bidnd it by overriding {{@link #bindUIProvider()}}
     */
    protected void define() {
        addUIBinding(DefaultApplicationUI.class);
    }

    protected void addUIBinding(Class<? extends ScopedUI> uIClass) {
        uiMapBinder.addBinding(uIClass.getName())
                   .toInstance(uIClass);
    }

    private void bindApplicationTitle() {
        ApplicationTitle title = new ApplicationTitle(applicationTitleKey());
        bind(ApplicationTitle.class).toInstance(title);
    }

    /**
     * override this method to provide the I18Nkey which defines your application title (which appears in your browser
     * tab)
     */
    protected I18NKey applicationTitleKey() {
        return LabelKey.Krail;
    }


    /**
     * Override to bind your ScopedUIProvider implementation
     */
    protected void bindUIProvider() {
        bind(ScopedUIProvider.class);
    }


}
