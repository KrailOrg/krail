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

package uk.q3c.krail.core.i18n;

import com.vaadin.ui.Button;
import uk.q3c.util.test.NotOnWeekends;

/**
 * Provides a byte-enhanced class using JPA @Transactional
 * <p>
 * Created by David Sowerby on 11/05/15.
 */
public class I18NTestClass4 {

    @Caption(caption = LabelKey.Authentication, description = DescriptionKey.Please_log_in)
    Button tba;

    @NotOnWeekends
    public void pretendData() {

    }
}
