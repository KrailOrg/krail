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

package uk.q3c.krail.core.view

import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey

/**
 * Created by David Sowerby on 09 Feb 2016
 */
class DefaultErrorViewTest extends ViewTest {

    DefaultErrorView thisView

    def setup() {
        thisView = new DefaultErrorView(translate)
        view = thisView
        fieldsWithoutCaptions = ['textArea']
    }

    def "set and get error"() {
        given:
        Throwable error = new RuntimeException();
        when:
        thisView.setError(error)

        then:
        thisView.getError() == error
    }

    def "do build without error set, shows info message"() {
        when:
        thisView.doBuild(null)

        then:
        thisView.getTextArea().getValue().equals('Error view has been called but no error has been set.  This should not happen')
    }

    def "do build with error set, shows error stack"() {
        given:
        Throwable error = new RuntimeException();
        thisView.setError(error)

        when:
        thisView.doBuild(null)

        then:
        thisView.getTextArea().getValue().contains('java.lang.RuntimeException')
    }

    def "keys"() {
        expect:
        thisView.getNameKey() == LabelKey.Error
        thisView.getDescriptionKey() == DescriptionKey.Error_Information
    }
}
