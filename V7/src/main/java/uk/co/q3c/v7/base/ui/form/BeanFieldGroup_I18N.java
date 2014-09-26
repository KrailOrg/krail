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
package uk.co.q3c.v7.base.ui.form;

import com.google.inject.Inject;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import uk.co.q3c.v7.i18n.I18NProcessor;

/**
 * Wraps a {@link BeanFieldGroup} with methods to provide equivalent functionality, but using V7's I18N Framework to
 * provide captions and descriptions for the Field components
 *
 * @param <T>
 */
public class BeanFieldGroup_I18N<T> {

    private final I18NProcessor i18NProcessor;
    private BeanFieldGroup<T> fieldGroup;

    @Inject
    protected BeanFieldGroup_I18N(I18NProcessor i18NProcessor) {
        super();
        this.i18NProcessor = i18NProcessor;
    }

    public BeanFieldGroup<T> getFieldGroup() {
        return fieldGroup;
    }

    public BeanItem<T> getBeanItem() {
        return fieldGroup.getItemDataSource();
    }

    @SuppressWarnings("unchecked")
    public void setBeanItem(BeanItem<T> beanItem) {
        if (fieldGroup == null) {
            fieldGroup = (BeanFieldGroup<T>) new BeanFieldGroup<>(beanItem.getBean()
                                                                          .getClass());
            fieldGroup.buildAndBindMemberFields(this);
            i18NProcessor.translate(this);
        }
        fieldGroup.setItemDataSource(beanItem);

    }

    public void setBean(T bean) {
        setBeanItem(new BeanItem<T>(bean));
    }

}
