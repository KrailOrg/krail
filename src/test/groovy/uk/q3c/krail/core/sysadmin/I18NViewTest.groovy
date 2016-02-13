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

package uk.q3c.krail.core.sysadmin

import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.MessageKey
import uk.q3c.krail.core.i18n.Translate
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.util.Experimental
/**
 * Created by David Sowerby on 19 Jan 2016
 */
class I18NViewTest extends Specification {


    UserNotifier userNotifier = Mock()
    Translate translate = Mock()

    def setup() {
    }


    def "marked as experimental"() {
        expect:
        I18NView.class.isAnnotationPresent(Experimental)
    }


    def "doBuild"() {
        given:
        I18NView view = new I18NView(userNotifier, translate)
        translate.from(MessageKey.Setup_I18NKey_export, LabelKey.Export) >> "instruction1 text"
        translate.from(MessageKey.All_Keys_exported) >> "instruction2 text"
        when:
        view.doBuild()

        then:
        view.getRootComponent() != null
        view.getInstructions1().getValue().equals("instruction1 text")
        view.getInstructions2().getValue().equals("\ninstruction2 text")
        view.getLocaleList() != null
    }

    def "export button clicked"() {
        given:
        I18NView view = new I18NView(userNotifier, translate)
        view.doBuild()

        when:
        view.getExportButton().click()

        then:
        userNotifier.notifyInformation(LabelKey.This_feature_has_not_been_implemented)

    }
}
