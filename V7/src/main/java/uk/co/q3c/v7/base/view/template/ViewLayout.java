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
package uk.co.q3c.v7.base.view.template;

import java.util.List;

import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.i18n.I18NListener;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * A further level of abstraction from the standard Vaadin Layout components, the implementations of this interface
 * provide some common composite layouts, typical for web (and other) applications. They must also implement
 * {@link I18NListener}, so that I18N changes are propagated to components contained in the layout. The core V7 library
 * only provide one implementation, {@link VerticalViewLayout}, but it is expected that others will be added as a
 * separate library.
 * 
 * @author David Sowerby 14 Aug 2013
 * 
 */
public interface ViewLayout extends I18NListener {

	/**
	 * Transfer components from source to this instance
	 * 
	 * @param source
	 * @return
	 */
	public int transferComponentsFrom(ViewLayout source);

	/**
	 * A safe copy of the components contained by this template
	 * 
	 * @return
	 */
	List<AbstractComponent> orderedComponents();

	/**
	 * Places a component at a specific index in the template. The exact interpretation of what the index represents is
	 * defined by each implementation. If the index exceeds what the layout would expect, no error is raised, and the
	 * component held even if it is not used. This is to allow switching between patterns without dropping components.
	 * During initial build, or when adding a series of components, it is generally more efficient to use
	 * {@link #addComponent(int, Component)}, but this method is useful where you just want to replace one or two
	 * components;
	 * 
	 * @param index
	 * @param component
	 */
	void setComponent(int index, AbstractComponent component);

	/**
	 * Adds a component, and allocates it the next available index. If the index exceeds what the layout would expect,
	 * no error is raised, and the component held even if it is not used. This is to allow switching between patterns
	 * without dropping components. During initial build, or when adding a series of components, it is generally more
	 * efficient to use this method than {@link #setComponent(int, Component)}
	 * 
	 * @param index
	 * @param component
	 * 
	 */
	void addComponent(AbstractComponent component);

	/**
	 * Assembles (or re-assembles) the layout in accordance with the supplied config
	 */
	public void assemble(ViewConfig config);

	/**
	 * Returns the Component at the root of the layout. This is placed in the {@link ScopedUI} to display the view
	 * layout and its contained components.
	 * 
	 * @return
	 */
	public Component getLayoutRoot();

	/**
	 * Each implementation is required to provide a default configuration object. This ensures that the defaults are
	 * relevant to the implementation.
	 * 
	 * @return
	 */
	public ViewConfig defaultConfig();
}
