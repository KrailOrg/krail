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
package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * This module maps urls to {@link V7View} classes. The mapping is provided by the SiteMap.
 * 
 * @author David Sowerby 6 Apr 2013
 * 
 */
public class ApplicationViewModule extends AbstractModule {

	protected ApplicationViewModule() {
		super();
	}

	@Override
	protected void configure() {

		MapBinder<String, V7View> mapbinder = MapBinder.newMapBinder(binder(), String.class, V7View.class);
		bindViews(mapbinder);
	}

	/**
	 * You will need to override this to provide the Views for your application. See {@link DemoViewModule} for an
	 * example.
	 * 
	 * @param mapbinder
	 */
	protected void bindViews(MapBinder<String, V7View> mapbinder) {

	}

}
