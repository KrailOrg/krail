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

package uk.q3c.krail.core.sysadmin;

import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.SubscribeTo;
import uk.q3c.krail.core.i18n.*;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.util.Experimental;

/**
 * Enables the export of I18NKeys to a database - or at least it will one day
 * Created by David Sowerby on 13/07/15.
 */
@Experimental
@Listener
@SubscribeTo(SessionBus.class)
public class I18NView extends Grid3x3ViewBase {

    private static Logger log = LoggerFactory.getLogger(I18NView.class);
    @Caption(caption = LabelKey.Export, description = DescriptionKey.Start_the_export_for_the_chosen_Locales)
    private Button exportButton;
    @Caption(caption = LabelKey.Progress, description = DescriptionKey.Export_progress)
    private Label exportStatus;
    private Label instructions1;
    private Label instructions2;
    @Caption(caption = LabelKey.Locales, description = DescriptionKey.List_of_Locales_to_export)
    private TextArea localeList;
    private Translate translate;
    private UserNotifier userNotifier;
    @Inject
    protected I18NView(UserNotifier userNotifier, Translate translate) {
        super(translate);
        this.userNotifier = userNotifier;
        this.translate = translate;
    }

    public Button getExportButton() {
        return exportButton;
    }

    public TextArea getLocaleList() {
        return localeList;
    }

    public Label getInstructions1() {
        return instructions1;
    }

    public Label getInstructions2() {
        return instructions2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        instructions1 = new Label();
        instructions2 = new Label();
        localeList = new TextArea();
        exportButton = new Button();
        exportButton.addClickListener(event -> export());
        exportStatus = new Label();
        VerticalLayout layout1 = new VerticalLayout(exportButton, exportStatus);

        setTopLeft(new VerticalLayout(instructions1, instructions2, localeList));
        setMiddleLeft(layout1);
        localeChanged(null);
    }


    protected void export() {

        userNotifier.notifyInformation(LabelKey.This_feature_has_not_been_implemented);
    }

    @Handler
    public void localeChanged(LocaleChangeBusMessage busMessage) {

        instructions1.setValue(translate.from(MessageKey.Setup_I18NKey_export, LabelKey.Export));
        instructions2.setValue('\n' + translate.from(MessageKey.All_Keys_exported));
    }


}
