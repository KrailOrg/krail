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

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;
import uk.q3c.krail.base.navigate.V7Navigator;
import uk.q3c.krail.base.push.Broadcaster;
import uk.q3c.krail.base.push.PushMessageRouter;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.I18NProcessor;
import uk.q3c.krail.i18n.Translate;

// @PreserveOnRefresh
@Theme("chameleon")
public class BasicUI extends ScopedUI {

    @Inject
    protected BasicUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
                      Broadcaster broadcaster, PushMessageRouter pushMessageRouter,
                      ApplicationTitle applicationTitle, Translate translate, CurrentLocale currentLocale,
                      I18NProcessor translator) {
        super(navigator, errorHandler, converterFactory, broadcaster, pushMessageRouter, applicationTitle, translate,
                currentLocale, translator);

    }

    @Override
    protected AbstractOrderedLayout screenLayout() {
        return new VerticalLayout(getViewDisplayPanel());
    }

    @Override
    protected String pageTitle() {
        return "V7 base";
    }

    @Override
    protected void processBroadcastMessage(String group, String message) {
        // TODO Auto-generated method stub

    }

}