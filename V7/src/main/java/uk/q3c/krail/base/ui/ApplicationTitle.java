/*
 * Copyright (C) 2014 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.base.ui;

import uk.q3c.krail.i18n.I18NKey;

/**
 * A wrapper to simplify binding to an interface applied to an enum (although a Guice binding can be made, the result
 * has to carry a @SuppressWarning in UI constructors, which is messy)
 *
 * @author David Sowerby
 * @date 19 Apr 2014
 */
public class ApplicationTitle {
    private final I18NKey<?> titleKey;

    public ApplicationTitle(I18NKey<?> titleKey) {
        super();
        this.titleKey = titleKey;
    }

    public I18NKey<?> getTitleKey() {
        return titleKey;
    }

}