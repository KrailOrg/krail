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

import com.vaadin.annotations.Push;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.config.ConfigurationException;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScope;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.push.Broadcaster;
import uk.q3c.krail.core.push.Broadcaster.BroadcastListener;
import uk.q3c.krail.core.push.PushMessageRouter;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.core.view.KrailViewHolder;
import uk.q3c.krail.i18n.*;

/**
 * The base class for all Krail UIs, it provides an essential part of the {@link UIScoped} mechanism. It also provides
 * support for Vaadin Server Push (but only if you annotate your sub-class with {@link Push}), by capturing broadcast
 * messages in {@link #processBroadcastMessage(String, String)} and passing them to the {@link PushMessageRouter}. For
 * a
 * full description of the Krail server push implementation see: https://sites.google.com/site/q3cjava/server-push
 *
 * @author David Sowerby
 * @date modified 31 Mar 2014
 */
@Listener
public abstract class ScopedUI extends UI implements KrailViewHolder, BroadcastListener {
    private static Logger log = LoggerFactory.getLogger(ScopedUI.class);
    protected final CurrentLocale currentLocale;
    private final Panel viewDisplayPanel;
    private final ErrorHandler errorHandler;
    private final ConverterFactory converterFactory;
    private final PushMessageRouter pushMessageRouter;
    private final Navigator navigator;
    private final ApplicationTitle applicationTitle;
    private final Translate translate;
    private final I18NProcessor translator;
    private UIKey instanceKey;

    private AbstractOrderedLayout screenLayout;
    private UIScope uiScope;
    private KrailView view;

    protected ScopedUI(Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory, Broadcaster broadcaster, PushMessageRouter
            pushMessageRouter, ApplicationTitle applicationTitle, Translate translate, CurrentLocale currentLocale, I18NProcessor translator) {
        super();
        this.errorHandler = errorHandler;
        this.navigator = navigator;
        this.converterFactory = converterFactory;
        this.pushMessageRouter = pushMessageRouter;
        this.applicationTitle = applicationTitle;
        this.translate = translate;
        this.translator = translator;
        this.currentLocale = currentLocale;
        viewDisplayPanel = new Panel();
        registerWithBroadcaster(broadcaster);

    }

    protected void registerWithBroadcaster(Broadcaster broadcaster) {
        broadcaster.register(Broadcaster.ALL_MESSAGES, this);
    }

    public UIKey getInstanceKey() {
        return instanceKey;
    }

    protected void setInstanceKey(UIKey instanceKey) {
        this.instanceKey = instanceKey;
    }

    protected void setScope(UIScope uiScope) {
        this.uiScope = uiScope;
    }

    @Override
    public void detach() {
        if (uiScope != null) {
            uiScope.releaseScope(instanceKey);
        }
        super.detach();
    }

    /**
     * The Vaadin navigator has been replaced by the Navigator, use {@link #getKrailNavigator()} instead.
     *
     * @see com.vaadin.ui.UI#getNavigator()
     */
    @Override
    @Deprecated
    public com.vaadin.navigator.Navigator getNavigator() {
        return null;
    }

    @Override
    public void setNavigator(com.vaadin.navigator.Navigator navigator) {
        throw new MethodReconfigured("UI.setNavigator() not available, use injection instead");
    }

    @Override
    public void changeView(KrailView toView) {
        if (log.isDebugEnabled()) {
            String to = (toView == null) ? "null" : toView.getClass()
                                                          .getSimpleName();
            log.debug("changing view to {}", to);
        }

        Component content = toView.getRootComponent();
        translator.translate(toView);
        content.setSizeFull();
        viewDisplayPanel.setContent(content);
        this.view = toView;
    }

    /**
     * Make sure you call this from sub-class overrides. The Vaadin Page is not available during the construction of
     * this class, but is available when this method is invoked. As a result, this method sets the navigator a listener
     * for URI changes and obtains the browser locale setting for initialising {@link CurrentLocale}. Both of these are
     * provided by the Vaadin Page.
     *
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {

        VaadinSession session = getSession();
        session.setConverterFactory(converterFactory);

        // page isn't available during injected construction, so we have to do this here
        Page page = getPage();
        page.addUriFragmentChangedListener(navigator);


        setErrorHandler(errorHandler);
        session.setErrorHandler(errorHandler);
        page.setTitle(pageTitle());

        //  also loads the UserSitemap if not already loaded
        getKrailNavigator().init();

        //layout this UI, which may also create UYI components
        doLayout();

        // now that browser is active, and user sitemap loaded, and UI constructed, set up currentLocale
        currentLocale.readFromEnvironment();
        translator.translate(this);
        // Navigate to the correct start point
        String fragment = getPage().getUriFragment();
        getKrailNavigator().navigateTo(fragment);
    }

    public Navigator getKrailNavigator() {
        return navigator;
    }

    /**
     * Provides a locale sensitive title for your application (which appears in the browser tab). The title is defined
     * by the {@link #applicationTitle}, which should be specified in your sub-class of {@link DefaultUIModule}
     *
     * @return locale sensitive page title
     */
    protected String pageTitle() {
        I18NKey key = applicationTitle.getTitleKey();
        return translate.from(key);
    }


    /**
     * Uses the {@link #screenLayout} defined by sub-class implementations of {@link #screenLayout()}, expands it to
     * full size, and sets the View display panel to take up all spare space.
     */
    protected void doLayout() {
        if (screenLayout == null) {
            screenLayout = screenLayout();
        }
        screenLayout.setSizeFull();
        if (viewDisplayPanel.getParent() == null) {
            String msg = "Your implementation of ScopedUI.screenLayout() must include getViewDisplayPanel().  AS a "
                    + "minimum this could be 'return new VerticalLayout(getViewDisplayPanel())'";
            log.error(msg);
            throw new ConfigurationException(msg);
        }
        viewDisplayPanel.setSizeFull();
        setContent(screenLayout);
    }

    /**
     * Override this to provide your screen layout. In order for Views to work one child component of this layout must
     * be provided by {@link #getViewDisplayPanel()}. The simplest example would be
     * {@code return new VerticalLayout(getViewDisplayPanel()}, which would set the View to take up all the available
     * screen space. {@link BasicUI} is an example of a UI which contains a header and footer bar.
     *
     * @return the layout in which views are placed
     */
    protected abstract AbstractOrderedLayout screenLayout();

    public Panel getViewDisplayPanel() {
        return viewDisplayPanel;
    }

    @Override
    public void receiveBroadcast(final String group, final String message) {
        access(new Runnable() {
            @Override
            public void run() {
                log.debug("receiving message: {}", message);
                processBroadcastMessage(group, message);

            }
        });
    }

    /**
     * Distribute the message to listeners within this UIScope
     */
    protected void processBroadcastMessage(String group, String message) {
        pushMessageRouter.messageIn(group, message);
    }

    /**
     * Responds to a locale change from {@link CurrentLocale} and updates the translation for this UI and the current
     * KrailView
     *
     * @param busMessage the message from the event bus.  Not actually used, as translate looks up the current locale
     */
    @SuppressWarnings("UnusedParameters")
    @Handler
    public void localeChanged(LocaleChangeBusMessage busMessage) {
        translator.translate(this);
        //during initial set up view has not been created but locale change gets called for other components
        if (getView() != null) {
            translator.translate(getView());
        }
    }

    public KrailView getView() {
        return view;
    }


}