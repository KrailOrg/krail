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

package uk.q3c.krail.core.ui

import com.vaadin.data.util.converter.ConverterFactory
import com.vaadin.server.ErrorHandler
import spock.lang.Specification
import uk.q3c.krail.core.i18n.CurrentLocale
import uk.q3c.krail.core.i18n.I18NProcessor
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.Translate
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.option.Option
import uk.q3c.krail.core.push.Broadcaster
import uk.q3c.krail.core.push.PushMessageRouter
import uk.q3c.krail.core.view.DefaultPublicHomeView
import uk.q3c.krail.testutil.i18n.MockTranslate
import uk.q3c.util.testutil.LogMonitor

/**
 * Created by David Sowerby on 09 Feb 2016
 */
class ScopedUITest2 extends Specification {

    ScopedUI ui
    Navigator navigator = Mock()
    ErrorHandler errorHandler = Mock()
    ConverterFactory converterFactory = Mock()
    Broadcaster broadcaster = Mock()
    PushMessageRouter pushMessageRouter = Mock()
    ApplicationTitle applicationTitle = Mock()
    Translate translate = new MockTranslate()
    CurrentLocale currentLocale = Mock()
    I18NProcessor translator = Mock()
    Option option = Mock()
    LogMonitor logMonitor;

    def setup() {
        logMonitor = new LogMonitor();
        logMonitor.addClassFilter(ScopedUI)
        applicationTitle.getTitleKey() >> LabelKey.Krail
        ui = new BasicUI(navigator, errorHandler, converterFactory, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator,
                option);
    }

    def cleanup() {
        logMonitor.close()
    }

    //Not a great test but cannot access Page title
    def "after view change, page title updated"() {
        given:
        DefaultPublicHomeView view = new DefaultPublicHomeView(translate)
        view.buildView(null)

        when:
        ui.changeView(view)

        then:
        logMonitor.debugLogs().contains("Page title set to 'Krail Unnamed'")
    }

    def "getNavigator() returns null"() {
        expect:
        ui.getNavigator() == null
    }
}
