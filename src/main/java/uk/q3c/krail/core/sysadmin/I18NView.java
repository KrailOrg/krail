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

package uk.q3c.krail.core.sysadmin;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Injector;
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
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.*;
import uk.q3c.krail.util.Experimental;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Enables the export of I18NKeys to a database
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
    private Injector injector;
    private Label instructions1;
    private Label instructions2;
    @Caption(caption = LabelKey.Locales, description = DescriptionKey.List_of_Locales_to_export)
    private TextArea localeList;
    private PatternUtility patternUtility;
    private Translate translate;
    private UserNotifier userNotifier;

    @Inject
    protected I18NView(PatternUtility patternUtility, UserNotifier userNotifier, Injector injector, Translate translate) {
        this.patternUtility = patternUtility;
        this.userNotifier = userNotifier;
        this.injector = injector;
        this.translate = translate;
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
        //        exportStatus.setValue("");
        //        Optional<DatabaseBundleWriter> writerOpt = findWriter();
        //        Set<Locale> locales = retrieveLocales();
        //        if (locales.isEmpty()) {
        //            userNotifier.notifyInformation(MessageKey.There_are_no_Locales_to_process);
        //            return;
        //        }
        //        if (writerOpt.isPresent()) {
        //            try {
        //                patternUtility.writeExclusive(locales, writerOpt.get());
        //                exportStatus.setValue(translate.from(MessageKey.Keys_exported, writerOpt.get()
        //                                                                                        .count(), locales.size()));
        //                userNotifier.notifyInformation(LabelKey.Export_complete);
        //            } catch (Exception e) {
        //                log.info("Export I18NKeys failed due to exception", e);
        //                userNotifier.notifyError(MessageKey.I18NKey_export_failed, e.getMessage());
        //            }
        //        }
    }

    @Handler
    public void localeChanged(LocaleChangeBusMessage busMessage) {

        instructions1.setValue(translate.from(MessageKey.Setup_I18NKey_export, LabelKey.Export));
        instructions2.setValue("\n" + translate.from(MessageKey.All_Keys_exported));
    }

    //    private Optional<DatabaseBundleWriter> findWriter() {
    //        exportStatus.setValue(translate.from(LabelKey.Looking_for_Database_Writer));
    //        Reflections reflections = new Reflections();
    //        final Set<Class<? extends DatabaseBundleWriter>> writers = reflections.getSubTypesOf(DatabaseBundleWriter.class);
    //        writers.remove(DatabaseBundleWriterBase.class);
    //        if (writers.size() == 1) {
    //            return Optional.of(injector.getInstance(writers.iterator()
    //                                                           .next()));
    //        }
    //        if (writers.size() == 0) {
    //            userNotifier.notifyWarning(MessageKey.Needs_at_least_one_database_writer);
    //            return Optional.empty();
    //        }
    //        userNotifier.notifyWarning(MessageKey.Currently_limited_to_supporting_one_database_writer);
    //        return Optional.empty();
    //    }

    @Nonnull
    protected Set<Locale> retrieveLocales() {
        exportStatus.setValue(translate.from(LabelKey.Retrieving_Locales));
        String userInput = localeList.getValue();
        List<String> localeTags = Splitter.on("\n")
                                          .trimResults()
                                          .omitEmptyStrings()
                                          .splitToList(userInput);
        Set<Locale> locales = new HashSet<>();
        localeTags.forEach(tag -> {
            try {
                Locale locale = new Locale.Builder().setLanguageTag(tag)
                                                    .build();
                locales.add(locale);
            } catch (IllformedLocaleException e) {
                userNotifier.notifyWarning(MessageKey.Invalid_Locale_Langugage_Tag, tag);
            }
        });
        return locales;
    }


}
