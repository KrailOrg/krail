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
package uk.co.q3c.v7.persist.form;

import uk.co.q3c.v7.base.ui.form.BeanFieldGroup_I18N;
import uk.co.q3c.v7.i18n.DefaultI18NProcessor;
import uk.co.q3c.v7.persist.EntityBase;

/**
 * A sub-class of {@link BeanFieldGroup_I18N} with type safety for descendants of {@link EntityBase}
 * 
 * @author David Sowerby 24 Mar 2013
 * 
 * @param <T>
 */
public class EntityFieldSet<T extends EntityBase> extends BeanFieldGroup_I18N<T> {

	protected EntityFieldSet(DefaultI18NProcessor translator) {
		super(translator);
	}

}
