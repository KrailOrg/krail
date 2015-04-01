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
package uk.q3c.krail.core.ui.form;

import com.google.inject.Inject;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.data.CommitException;
import uk.q3c.krail.core.data.KrailEntity;
import uk.q3c.krail.core.data.StatementDao;
import uk.q3c.krail.i18n.I18NProcessor;


/**
 * Wraps a {@link BeanFieldGroup} with methods to provide equivalent functionality, but using Krail's I18N Framework to
 * provide captions and descriptions for the Field components
 *
 * @param <E> the Entity type
 */
public class BeanFieldGroup_I18N<E extends KrailEntity<ID, VER>, ID, VER> {

    private static Logger log = LoggerFactory.getLogger(BeanFieldGroup_I18N.class);
    private final I18NProcessor i18NProcessor;
    private E bean;
    private StatementDao<ID, VER> dao;
    private BeanFieldGroup<E> fieldGroup;

    @Inject
    protected BeanFieldGroup_I18N(I18NProcessor i18NProcessor, StatementDao<ID, VER> dao) {
        super();
        this.i18NProcessor = i18NProcessor;
        this.dao = dao;
    }

    public BeanFieldGroup<E> getFieldGroup() {
        return fieldGroup;
    }

    public BeanItem<E> getBeanItem() {
        return fieldGroup.getItemDataSource();
    }

    @SuppressWarnings("unchecked")
    public void setBeanItem(BeanItem<E> beanItem) {
        if (fieldGroup == null) {
            fieldGroup = (BeanFieldGroup<E>) new BeanFieldGroup<>(beanItem.getBean()
                                                                          .getClass());
            fieldGroup.buildAndBindMemberFields(this);
            i18NProcessor.translate(this);
        }
        fieldGroup.setItemDataSource(beanItem);

    }

    public void setBean(E bean) {
        this.bean = bean;
        setBeanItem(new BeanItem<E>(bean));
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
            dao.save(bean);
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
