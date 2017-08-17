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

package uk.q3c.krail.core.sysadmin.option

import spock.lang.Specification
import uk.q3c.krail.core.eventbus.SessionBus
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.option.OptionPopup
import uk.q3c.krail.core.option.VaadinOptionSource
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.option.Option
import uk.q3c.krail.persist.PersistenceInfo
import uk.q3c.krail.persist.inmemory.InMemory

import java.lang.annotation.Annotation

/**
 * Tests for {@link SelectedOptionSourcePanel} and its super class SourcePanel
 *
 * Created by David Sowerby on 07/07/15.
 */


class SelectedOptionSourcePanelTest extends Specification {

    Translate translate = Mock()
    VaadinOptionSource optionSource = Mock()
    PersistenceInfo persistenceInfo = Mock()
    PersistenceInfo persistenceInfo2 = Mock()
    Option option = Mock()
    OptionPopup optionPopup = Mock()

    def panel = new SelectedOptionSourcePanel(translate, optionSource, option, optionPopup)


    def setup() {
        translate.from(LabelKey.Authorisation) >> "Authorisation"
        translate.from(LabelKey.Authentication) >> "Authentication"
        translate.from(LabelKey.Yes) >> "Yes"
        translate.from(LabelKey.No) >> "No"

        persistenceInfo.getName() >> LabelKey.Authentication
        persistenceInfo.getDescription() >> LabelKey.Authentication
        persistenceInfo.getConnectionUrl() >> "url"
        persistenceInfo.isVolatilePersistence() >> true

        persistenceInfo2.getName() >> LabelKey.Authorisation
    }

    def "if getSelectedSource() is called when selected source is null, selected source is set to optionSource activeSource "() {

        given:

        optionSource.getActiveSource() >> InMemory.class

        when:

        Class<? extends Annotation> actual = panel.getSelectedSource()

        then:

        actual == InMemory.class

    }

    def "doSetPersistenceInfo gets info from optionSource using selected source"() {

        given:

        optionSource.getActiveSource() >> InMemory.class
        optionSource.getPersistenceInfo(InMemory.class) >> persistenceInfo

        when:

        panel.doSetPersistenceInfo()

        then:

        panel.getPersistenceInfo() == persistenceInfo

    }

    def "when selected source is changed, then persistence info is reloaded and display refreshed"() {

        given:

        optionSource.getActiveSource() >>> [InMemory.class, SessionBus.class]
        optionSource.getPersistenceInfo(InMemory.class) >> persistenceInfo
        optionSource.getPersistenceInfo(SessionBus.class) >> persistenceInfo2



        when:

        panel.setSelectedSource(SessionBus.class)

        then:

        panel.getPersistenceInfo() == persistenceInfo2
        panel.getNameLabel().getValue() == "Authorisation"

    }


    def "When displayInfo() is called, all display elements are refreshed"() {

        given:

        optionSource.getActiveSource() >>> [InMemory.class, SessionBus.class]
        optionSource.getPersistenceInfo(InMemory.class) >> persistenceInfo

        when:

        panel.displayInfo()

        then:

        panel.getNameLabel().getValue() == "Authentication"
        panel.getDescriptionLabel().getValue() == "Authentication"
        panel.getConnectionUrlLabel().getValue() == "url"
        panel.getVolatileLabel().getValue() == "Yes"
    }

//    def "When styles() called, all components have default style except when overridden"() {
//
//        given:
//
//
//        optionSource.getActiveSource() >>> [InMemory.class, SessionBus.class]
//        optionSource.getPersistenceInfo(InMemory.class) >> persistenceInfo
//
//        // option.get() always returns default value
//        option.get(SourcePanel.connectionUrlValueStyleOptionKey) >> "large"
//        option.get(_) >> { OptionKey optionKey -> optionKey.getDefaultValue() }
//
//
//        when:
//
//        // to trigger style update
//        panel.optionValueChanged(null)
//
//        then:
//
//        def defaultValueStyleName = SourcePanel.defaultValueStyleOptionKey.getDefaultValue();
//
//        panel.getNameLabel().getStyleName() == defaultValueStyleName
//        panel.getDescriptionLabel().getStyleName() == defaultValueStyleName
//        panel.getConnectionUrlLabel().getStyleName() == "large"
//        panel.getVolatileLabel().getStyleName() == defaultValueStyleName
//
//        def defaultCaptionStyleName = SourcePanel.defaultCaptionStyleOptionKey.getDefaultValue();
//
//        panel.getNameCaption().getStyleName() == defaultCaptionStyleName
//        panel.getDescriptionCaption().getStyleName() == defaultCaptionStyleName
//        panel.getConnectionUrlCaption().getStyleName() == defaultCaptionStyleName
//        panel.getVolatileCaption().getStyleName() == defaultCaptionStyleName
//
//    }


}