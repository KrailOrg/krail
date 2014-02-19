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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.V7Navigator;

import com.google.inject.Inject;
import com.vaadin.ui.Component;

public abstract class ViewBase implements V7View {

	private static Logger log = LoggerFactory.getLogger(ViewBase.class);
	private final V7Navigator navigator;
	protected Component rootComponent;

	@Inject
	protected ViewBase(V7Navigator navigator) {
		super();
		this.navigator = navigator;

	}

	protected ViewBase() {
		navigator = null;
	}

	/**
	 * You only need to implement this method if you are using TestBench, or another testing tool which looks for debug
	 * ids. If you do override it to add your own subclass ids, make sure you call super
	 */
	@Override
	public void setIds() {
		rootComponent.setId(ID.getId(this, rootComponent));
	}

	@Override
	public void enter(V7ViewChangeEvent event) {
		log.debug("entered view: " + this.getClass().getSimpleName() + " with uri "
				+ navigator.getCurrentNavigationState());
		List<String> params = navigator.getNavigationParams();
		processParams(params);
	}

	/**
	 * This method is called with the URI parameters separated from the "address" part of the URI, and is typically used
	 * to set up the state of a view in response to the parameter values
	 * 
	 * @param params
	 */
	protected abstract void processParams(List<String> params);

	public V7Navigator getNavigator() {
		return navigator;
	}

	@Override
	public Component getRootComponent() {
		return rootComponent;
	}

	@Override
	public String viewName() {
		return getClass().getSimpleName();
	}

}
