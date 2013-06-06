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
package uk.co.q3c.v7.demo.ui.form;

import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.I18NListener;
import uk.co.q3c.v7.i18n.I18NTranslator;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;

public class BeanFieldSet<T> implements I18NListener {
	private BeanFieldGroup<T> fieldGroup;
	private final AnnotationI18NTranslator translator;

	protected BeanFieldSet(AnnotationI18NTranslator translator) {
		super();
		this.translator = translator;
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
			fieldGroup = (BeanFieldGroup<T>) new BeanFieldGroup<>(beanItem.getBean().getClass());
			fieldGroup.buildAndBindMemberFields(this);
			translator.translate(this);
		}
		fieldGroup.setItemDataSource(beanItem);

	}

	public void setBean(T bean) {
		setBeanItem(new BeanItem<T>(bean));
	}

	@Override
	public void localeChange(I18NTranslator translator) {
		translator.translate(this);
	}

}
