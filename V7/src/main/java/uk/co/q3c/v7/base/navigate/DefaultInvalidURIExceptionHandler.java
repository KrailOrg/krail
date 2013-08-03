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
package uk.co.q3c.v7.base.navigate;

import javax.inject.Inject;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public class DefaultInvalidURIExceptionHandler implements InvalidURIExceptionHandler {

	private final V7Navigator navigator;

	@Inject
	protected DefaultInvalidURIExceptionHandler(V7Navigator navigator) {
		super();
		this.navigator = navigator;
	}

	// TODO I18N
	@Override
	public void invoke() {
		Notification n = new Notification("Invalid page", navigator.getNavigationState() + " is not a valid page",
				Notification.Type.HUMANIZED_MESSAGE, false);
		n.show(Page.getCurrent());
	}

}
