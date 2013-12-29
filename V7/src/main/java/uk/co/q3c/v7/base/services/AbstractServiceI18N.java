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
package uk.co.q3c.v7.base.services;

import com.google.inject.Inject;

import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

/**
 * Extends AbstractService to provide I18N support for name and description fields
 * 
 * @author David Sowerby
 * 
 */
public abstract class AbstractServiceI18N extends AbstractService implements ServiceI18N {
	private I18NKey<?> nameKey;
	private I18NKey<?> descriptionKey;
	private final Translate translate;

	@Inject
	protected AbstractServiceI18N(Translate translate) {
		super();
		this.translate = translate;
	}

	@Override
	public I18NKey<?> getNameKey() {
		return nameKey;
	}

	@Override
	public I18NKey<?> getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public String getName() {
		return translate.from(nameKey);
	}

	/**
	 * returns the translation for {@code #descriptionKey}, or an empty String if {@link #descriptionKey} is null - this
	 * makes the descriptionKey optional
	 * 
	 * @see uk.co.q3c.v7.base.services.Service#getDescription()
	 */
	@Override
	public String getDescription() {
		if (descriptionKey == null) {
			return "";
		}
		return translate.from(descriptionKey);
	}

	@Override
	public void setNameKey(I18NKey<?> nameKey) {
		this.nameKey = nameKey;
	}

	@Override
	public void setDescriptionKey(I18NKey<?> descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

}
