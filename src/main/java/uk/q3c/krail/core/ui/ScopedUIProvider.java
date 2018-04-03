/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.ui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIKeyProvider;
import uk.q3c.krail.core.guice.uiscope.UIScope;
import uk.q3c.krail.core.guice.uiscope.UIScoped;

import java.io.Serializable;
import java.util.Map;

/**
 * A Vaadin UI provider which supports the use of Guice scoped UI (see {@link UIScoped}). If you do not need UIScope,
 * then just extend from UIProvider directly
 * <p>
 * Subclasses should implement getUIClass(UIClassSelectionEvent event) to provide logic for selecting the UI class.
 *
 * @author David Sowerby, Will Temperley
 */
@SuppressFBWarnings("SE_BAD_FIELD")
public class ScopedUIProvider extends UIProvider implements Provider<ScopedUI>, Serializable {
    private static Logger log = LoggerFactory.getLogger(ScopedUIProvider.class);
    protected transient UIKeyProvider uiKeyProvider;
    private transient Map<String, Class<? extends ScopedUI>> uiMapBinder;
    private transient Map<String, Provider<ScopedUI>> uiMapBinderProvider;

    @Inject
    protected void init(UIKeyProvider uiKeyProvider, Map<String, Class<? extends ScopedUI>> uiMapBinder, Map<String, Provider<ScopedUI>> uiMapBinderProvider) {
        this.uiKeyProvider = uiKeyProvider;
        this.uiMapBinder = uiMapBinder;
        this.uiMapBinderProvider = uiMapBinderProvider;
    }

    /**
     * Default implementation assumes that only one ScopedUI implementation is used in the application, and it is held in {@link #uiMapBinder}.  If more than
     * entry is present in {@link #uiMapBinder}, the selection is indeterminate, and a warning is logged.
     * <p>
     * Override this method to provide logic for selecting from multiple UIs, then change the binging for ScopedUIProvider in a sub-class of {@link
     * DefaultUIModule}
     *
     * @param event
     *         the event which triggers the selection of a UI
     *
     * @return the class of the selected UI;  in this implementation, this is the only or first selected from {@link #uiMapBinder}
     */
    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        if (uiMapBinder.isEmpty()) {
            throw new UIProviderException("At least one UI must be defined in the UIModule, uiBinder");
        }
        if (uiMapBinder.size() > 1) {
            log.warn("More than one UI class has been defined, but there is no logic to determine which to use.");
        }
        return uiMapBinder.get(uiMapBinder.keySet()
                                          .iterator()
                                          .next());
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        Class<? extends UI> uiClass = event.getUIClass();
        UIKey uiKey = uiKeyProvider.get();
        // hold the key while UI is created
        CurrentInstance.set(UIKey.class, uiKey);
        // and set up the scope
        UIScope scope = UIScope.getCurrent();
        scope.startScope(uiKey);
        // create the UI
        Provider<ScopedUI> provider = uiMapBinderProvider.get(event.getUIClass().getName());
        ScopedUI ui = provider.get();
        ui.setInstanceKey(uiKey);
        ui.setScope(scope);

        log.debug("Returning instance of {} with key {}", uiClass.getName(), uiKey);
        return ui;
    }

    @Override
    public ScopedUI get() {
        return (ScopedUI) UI.getCurrent();
    }
}
