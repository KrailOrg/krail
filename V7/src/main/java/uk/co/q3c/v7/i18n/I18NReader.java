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

import java.lang.annotation.Annotation;

/**
 * Reads the keys for this an {@link I18N} annotation
 * 
 * @author David Sowerby 3 Mar 2013
 * 
 */
public class I18NReader implements I18NAnnotationReader {

	protected I18NReader() {
		super();
	}

	@Override
	public I18NKeys<?> caption(Annotation annotation) {
		return ((I18N) annotation).caption();
	}

	@Override
	public I18NKeys<?> description(Annotation annotation) {
		return ((I18N) annotation).description();
	}

	@Override
	public I18NKeys<?> value(Annotation annotation) {
		return ((I18N) annotation).value();
	}

}
