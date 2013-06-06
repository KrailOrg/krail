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

import uk.co.q3c.v7.demo.data.EntityBase;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;

/**
 * A sub-class of {@link BeanFieldSet} with type safety for descendants of {@link EntityBase}
 * 
 * @author David Sowerby 24 Mar 2013
 * 
 * @param <T>
 */
public class EntityFieldSet<T extends EntityBase> extends BeanFieldSet<T> {

	protected EntityFieldSet(AnnotationI18NTranslator translator) {
		super(translator);
	}

}
