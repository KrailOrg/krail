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
package fixture.testviews2;

import uk.co.q3c.v7.base.navigate.sitemap.View;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;

import com.vaadin.ui.Component;

/**
 * This is NOT UIScoped to avoid unnecessary complexity in setting up some of the tests - if you need a UIScoped test
 * view you will need to create a different one
 * 
 * @author David Sowerby
 * 
 */
@View(uri = "a/b", labelKeyName = "Transfers", isPublic = false, permission = "admin")
public class TestAnnotatedView implements V7View {

	@Override
	public void enter(V7ViewChangeEvent event) {
	}

	@Override
	public Component getRootComponent() {

		return null;
	}

	@Override
	public String viewName() {

		return "Test annotated view";
	}

}
