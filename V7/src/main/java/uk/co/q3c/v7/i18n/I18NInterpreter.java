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
package uk.co.q3c.v7.i18n;

import java.util.Locale;

/**
 * Implementers capture I18N key information and provide Vaadin components in the I18NListener with translated caption,
 * description and value. (Each of these is optional)
 * 
 * @author David Sowerby 10 Feb 2013
 * 
 */
public interface I18NInterpreter {

	/**
	 * Look up keys, translate to values for the {@link CurrentLocale#getLocale()}, and apply them to all selected
	 * Vaadin components. Also applies the locale to each of the components
	 * 
	 * @param listener
	 */
	public abstract void interpret(I18NListener listener);

	/**
	 * The Locale being used for translation
	 * 
	 * @return
	 */
	public abstract Locale getLocale();

}