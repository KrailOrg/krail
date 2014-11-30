/*
 * Copyright (C) 2013 David Sowerby
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
package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

public abstract class ViewBaseI18N extends ViewBase {

    private final Translate translate;
    private I18NKey nameKey;

    @Inject
    protected ViewBaseI18N(Translate translate) {
        super();
        this.translate = translate;
    }

    public I18NKey getNameKey() {
        return nameKey;
    }

    public <E extends Enum<E> & I18NKey> void setNameKey(E nameKey) {
        this.nameKey = nameKey;
    }

    @Override
    public String viewName() {
        return translateKey();
    }

    private <E extends Enum<E> & I18NKey> String translateKey() {
        return translate.from((E) nameKey);
    }
}
