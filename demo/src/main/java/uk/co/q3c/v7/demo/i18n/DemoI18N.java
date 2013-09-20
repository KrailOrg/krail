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
package uk.co.q3c.v7.demo.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.data.Property;

/**
 * Annotation used for marking a Vaadin UI component in the demo app as needing
 * I18N translation. The parameters provide the keys for I18N lookup. All
 * parameters are optional, but the value parameter is relevant only for those
 * components which implement {@link Property}. Its value would be ignored
 * otherwise
 * 
 * @see https://sites.google.com/site/q3cjava/internationalisation-i18n
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DemoI18N {
	DemoLabelKey caption() default DemoLabelKey._nullkey_;

	DemoLabelKey description() default DemoLabelKey._nullkey_;

	/**
	 * Usually only used with Vaadin Labels
	 * 
	 * @return
	 */
	DemoLabelKey value() default DemoLabelKey._nullkey_;
}
