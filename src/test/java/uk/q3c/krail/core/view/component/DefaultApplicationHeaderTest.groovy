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

package uk.q3c.krail.core.view.component

import com.vaadin.ui.HorizontalLayout
import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.Translate
import uk.q3c.krail.core.ui.ApplicationTitle

/**
 * Created by David Sowerby on 08 Feb 2016
 */
class DefaultApplicationHeaderTest extends Specification {

    DefaultApplicationHeader header
    Translate translate = Mock()
    ApplicationTitle applicationTitle

    def setup() {
        applicationTitle = new ApplicationTitle(LabelKey.Krail)
        translate.from(LabelKey.Krail) >> 'Krail'
        header = new DefaultApplicationHeader(applicationTitle, translate)
    }

    def "construct"() {
        expect:
        header.getContent() instanceof HorizontalLayout
        HorizontalLayout layout = header.getContent()
        layout.getComponent(0) == header.getLabel()
        header.getId() != null
        header.getLabel().getId() != null
        header.getLabel().getValue().equals('Krail')

    }
}
