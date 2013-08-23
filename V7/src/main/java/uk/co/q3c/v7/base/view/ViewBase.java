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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.layout.ViewBaseWithLayout;

public abstract class ViewBase implements V7View {

	private static Logger log = LoggerFactory.getLogger(ViewBase.class);
	private final V7Navigator navigator;

	@Inject
	protected ViewBase(V7Navigator navigator) {
		super();
		this.navigator = navigator;
		buildUI();
		assemble();
	}

	/**
	 * This method is called after {@link #buildUI()}, and is primarily intended for use by descendant class
	 * {@link ViewBaseWithLayout}.
	 */
	protected void assemble() {
	}

	/**
	 * Implement this method to create and assemble the user interface components for the view. If you use sub-class
	 * {@link ViewBaseWithLayout} there are some useful shorthand methods for creating and sizing components
	 */
	protected abstract void buildUI();

	@Override
	public void enter(V7ViewChangeEvent event) {
		log.debug("entered view: " + this.getClass().getSimpleName() + "with uri " + navigator.getNavigationState());
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

}
