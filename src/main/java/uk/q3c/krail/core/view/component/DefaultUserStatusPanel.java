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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.SessionBusProvider;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.status.UserStatusBusMessage;
import uk.q3c.krail.core.user.status.UserStatusChangeSource;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

/**
 * Represents the "logged in" status of the current {@link Subject}.
 * <p>
 *
 * @author David Sowerby 16 Jan 2013
 */
// TODO I18N
@Listener
@SubscribeTo(SessionBus.class)
@AssignComponentId()
public class DefaultUserStatusPanel extends Panel implements UserStatusPanel, ClickListener, UserStatusChangeSource {
    private static Logger log = LoggerFactory.getLogger(DefaultUserStatusPanel.class);
    private final Label usernameLabel;
    private final Button login_logout_Button;
    private final Navigator navigator;
    private final Provider<Subject> subjectProvider;
    private final Translate translate;
    private final SubjectIdentifier subjectIdentifier;
    private final PubSubSupport<BusMessage> eventBus;
    private final CurrentLocale currentLocale;

    @Inject
    protected DefaultUserStatusPanel(Navigator navigator, SubjectProvider subjectProvider, Translate translate, SubjectIdentifier subjectIdentifier,
                                     SessionBusProvider eventBusProvider, CurrentLocale currentLocale) {
        super();
        this.navigator = navigator;
        this.subjectProvider = subjectProvider;
        this.translate = translate;
        this.subjectIdentifier = subjectIdentifier;
        this.eventBus = eventBusProvider.get();
        this.currentLocale = currentLocale;
        //        eventBus.subscribe(this);
        setSizeFull();
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        usernameLabel = new Label();
        login_logout_Button = new Button();
        login_logout_Button.addClickListener(this);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(usernameLabel);
        hl.addComponent(login_logout_Button);
        this.setContent(hl);
        build();

    }

    protected final void build() {
        log.debug("building with Locale={}", currentLocale.getLocale());
        boolean authenticated = subjectProvider.get()
                                               .isAuthenticated();
        String caption = (authenticated) ? translate.from(LabelKey.Log_Out) : translate.from(LabelKey.Log_In);
        log.debug("Caption is '{}'", caption);
        login_logout_Button.setCaption(caption.toLowerCase());
        usernameLabel.setValue(subjectIdentifier.subjectName());
    }


    /**
     * Responds to the {@code busMessage} by rebuilding the panel to reflect a change in user status.
     *
     * @param busMessage
     *         the message received from the event bus
     */
    @Handler
    public void userStatusChange(UserStatusBusMessage busMessage) {
        log.debug("user status has changed to authenticated = '{}', reset the user status panel", busMessage.isAuthenticated());
        build();
    }

    @Override
    public String getActionLabel() {
        return login_logout_Button.getCaption();
    }

    @Override
    public String getUserId() {
        return usernameLabel.getValue();
    }

    public Button getLogin_logout_Button() {
        return login_logout_Button;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        boolean authenticated = subjectProvider.get()
                                               .isAuthenticated();
        if (authenticated) {
            subjectProvider.get()
                           .logout();
            log.debug("Publishing UserStatusBusMessage from: '{}'", this.getClass()
                                                                        .getSimpleName());
            eventBus.publish(new UserStatusBusMessage(this, false));
        } else {
            navigator.navigateTo(StandardPageKey.Log_In);
        }

    }

    @Handler
    public void localeChanged(LocaleChangeBusMessage busMessage) {
        log.debug("locale change to {}", busMessage.getNewLocale());
        build();
    }
}
