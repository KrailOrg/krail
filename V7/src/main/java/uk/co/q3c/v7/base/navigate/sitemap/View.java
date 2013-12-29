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
package uk.co.q3c.v7.base.navigate.sitemap;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import uk.co.q3c.v7.base.view.V7View;

/**
 * An annotation used to map a uri to a {@link V7View} implementation, the name of the key for an I18N label and a flag
 * to indicate whether or not this uri is "public" (does not require the user to be authorised). Permission is the
 * permission required in order to access this view. Used by a {@link AnnotationSitemapModule} to scan views for the
 * {@link AnnotationSitemapLoader} to load into the {@link Sitemap}.
 * 
 * @author David Sowerby
 * 
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
public @interface View {

	String uri();

	boolean isPublic() default false;

	String labelKeyName();

	String permission() default "";
}
