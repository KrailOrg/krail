/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.demo;

import com.google.inject.Inject;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.push.PushMessageRouter;
import uk.co.q3c.v7.base.ui.ApplicationTitle;
import uk.co.q3c.v7.base.ui.DefaultApplicationUI;
import uk.co.q3c.v7.base.user.notify.UserNotifier;
import uk.co.q3c.v7.base.view.component.*;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NProcessor;
import uk.co.q3c.v7.i18n.Translate;

/**
 * The UI class used in this demo for the V7 application base
 *
 * @author David Sowerby
 */
@Theme("chameleon")
@Push
public class DemoUI extends DefaultApplicationUI {

    @Inject
    protected DemoUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
                     ApplicationLogo logo, ApplicationHeader header, UserStatusPanel userStatus,
                     UserNavigationMenu menu, UserNavigationTree navTree, Breadcrumb breadcrumb,
                     SubPagePanel subpage, MessageBar messageBar, Broadcaster broadcaster,
                     PushMessageRouter pushMessageRouter, Translate translate, ApplicationTitle applicationTitle,
                     CurrentLocale currentLocale, I18NProcessor translator, LocaleSelector localeSelector,
                     UserNotifier userNotifier) {
        super(navigator, errorHandler, converterFactory, logo, header, userStatus, menu, navTree, breadcrumb,
                subpage, messageBar, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale,
                translator, localeSelector, userNotifier);

    }

}