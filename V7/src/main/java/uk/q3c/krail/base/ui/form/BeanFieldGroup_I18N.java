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
package uk.q3c.krail.base.ui.form;

import com.google.inject.Inject;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.base.data.CommitException;
import uk.q3c.krail.base.data.Entity;
import uk.q3c.krail.i18n.I18NProcessor;


/**
 * Wraps a {@link BeanFieldGroup} with methods to provide equivalent functionality, but using V7's I18N Framework to
 * provide captions and descriptions for the Field components
 *
 * @param <T>
 */
public class BeanFieldGroup_I18N<T extends Entity> {

    private static Logger log = LoggerFactory.getLogger(BeanFieldGroup_I18N.class);
    private final I18NProcessor i18NProcessor;
    private T bean;
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
        this.bean = bean;
        setBeanItem(new BeanItem<T>(bean));
    }

    /**
     * Commits changes from the UI to the backing bean.
     *
     * @throws CommitException
     *         if the commit fails
     */
    public void commit() throws CommitException {
        try {
            fieldGroup.commit();
            bean.save();
        } catch (FieldGroup.CommitException e) {
            log.error("Unable to save changes", e);
            throw new CommitException(e);
        }
    }

    /**
     * Cancels changes to the backing bean, and re-instates original values in Fields
     */
    public void cancel() {
        fieldGroup.discard();
    }

}
