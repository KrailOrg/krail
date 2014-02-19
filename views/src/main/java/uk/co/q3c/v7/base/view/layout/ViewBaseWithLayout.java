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
package uk.co.q3c.v7.base.view.layout;

import java.util.List;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.ViewBase;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Uses a {@link ViewLayout} instance to provide component layout, a {@link ViewConfig} instance to provide
 * configuration and component default settings.
 * <p>
 * Provides a number of methods to build a chain (using the internal class {@link ComponentWrapper}), for example:
 * <p>
 * add(Button).width(50).height("20%").caption("neat");
 * <p>
 * To change config: getConfig(), make changes, and then call assemble() to implement changes
 */
public abstract class ViewBaseWithLayout extends ViewBase implements ViewWithLayout {
	private ViewLayout layout;
	private ViewConfig config;
	private final Translate translate;

	protected class ComponentWrapper {
		private final Component component;

		protected ComponentWrapper(Component component) {
			super();
			this.component = component;
		}

		public Component getComponent() {
			return component;
		}

		public ComponentWrapper caption(String caption) {
			component.setCaption(caption);
			return this;
		}

		public ComponentWrapper caption(I18NKey<?> key) {
			component.setCaption(translate.from(key));
			return this;
		}

		/**
		 * Height in the normal Vaadin format
		 * 
		 * @param height
		 * @return
		 */
		public ComponentWrapper height(String height) {
			component.setHeight(height);
			return this;
		}

		/**
		 * Assumes height is in pixels
		 * 
		 * @param height
		 * @return
		 */
		public ComponentWrapper height(int height) {
			component.setHeight(height + "px");
			return this;
		}

		/**
		 * Height in percentage
		 * 
		 * @param height
		 * @return
		 */
		public ComponentWrapper heightPercent(int height) {
			component.setHeight(height + "%");
			return this;
		}

		/**
		 * Width in the normal Vaadin format
		 * 
		 * @param width
		 * @return
		 */
		public ComponentWrapper width(String width) {
			component.setWidth(width);
			return this;
		}

		/**
		 * Assumes width is in pixels
		 * 
		 * @param width
		 * @return
		 */
		public ComponentWrapper width(int width) {
			component.setWidth(width + Unit.PIXELS.getSymbol());
			return this;
		}

		/**
		 * Sets width to undefined and units to pixels
		 * 
		 * @return
		 */
		public ComponentWrapper widthUndefined() {
			component.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PIXELS);
			return this;
		}

		/**
		 * Sets width and height to undefined and units to pixels
		 * 
		 * @return
		 */
		public ComponentWrapper sizeUndefined() {
			component.setSizeUndefined();
			return this;
		}

		/**
		 * Width in percentage
		 * 
		 * @param width
		 * @return
		 */
		public ComponentWrapper widthPercent(int width) {
			component.setWidth(width + Unit.PERCENTAGE.getSymbol());
			return this;
		}

		/**
		 * Uses {@link Component#addStyleName(String)}. To use setStyleName use {@link #setStyle(String)}
		 * 
		 * @param styleName
		 * @return
		 */
		public ComponentWrapper style(String styleName) {
			component.addStyleName(styleName);
			return this;
		}

		/**
		 * Uses {@link Component#setStyleName(String)}. To use addStyleName use {@link #style(String)}
		 * 
		 * @param styleName
		 * @return
		 */
		public ComponentWrapper setStyle(String styleName) {
			component.setStyleName(styleName);
			return this;
		}

		public ComponentWrapper id(String id) {
			component.setId(id);
			return this;
		}

		public ComponentWrapper visible() {
			component.setVisible(true);
			return this;
		}

		public ComponentWrapper notVisible() {
			component.setVisible(false);
			return this;
		}

		/**
		 * If the component is a subclass of {@link AbstractComponent}, setImmediate(true), otherwise ignore the call
		 * 
		 * @return
		 */
		public ComponentWrapper immediate() {
			if (component instanceof AbstractComponent) {
				AbstractComponent c = (AbstractComponent) component;
				c.setImmediate(true);
			}
			return this;
		}

		/**
		 * If the component is a subclass of {@link AbstractComponent}, setImmediate(true), otherwise ignore the call
		 * 
		 * @return
		 */
		public ComponentWrapper notImmediate() {
			if (component instanceof AbstractComponent) {
				AbstractComponent c = (AbstractComponent) component;
				c.setImmediate(false);
			}
			return this;
		}

		public ComponentWrapper enabled() {
			component.setEnabled(true);
			return this;
		}

		public ComponentWrapper disabled() {
			component.setEnabled(false);
			return this;
		}

	}

	@Inject
	protected ViewBaseWithLayout(V7Navigator navigator, ViewLayout viewLayout, Translate translate) {
		super(navigator);
		this.translate = translate;
		this.layout = viewLayout;
		this.config = layout.defaultConfig();
		layout.setConfig(config);
	}

	@Override
	public ViewConfig getConfig() {
		return config;
	}

	/**
	 * @see ViewLayout#addComponent(Component)
	 * @param component
	 * @return
	 */
	public Component addComponent(Component component) {
		config.setDefaults(component);
		layout.addComponent(component);
		return component;
	}

	public ComponentWrapper add(Component component) {
		addComponent(component);
		ComponentWrapper wrapper = new ComponentWrapper(component);
		return wrapper;
	}

	public ViewLayout getLayout() {
		return layout;
	}

	public void setLayout(ViewLayout layout) {
		this.layout = layout;
	}

	/**
	 * Sets the config to use for this instance, and the layout it contains
	 * 
	 * @param config
	 */
	public void setConfig(ViewConfig config) {
		this.config = config;
		layout.setConfig(config);
	}

	@Override
	public Component getRootComponent() {
		layout.assemble();
		rootComponent = layout.getLayoutRoot();
		return rootComponent;
	}

	public List<Component> orderedComponents() {
		return layout.orderedComponents();
	}

	public Component addComponent(Class<? extends Component> clazz) {
		try {
			Component c = clazz.newInstance();
			layout.addComponent(c);
			return c;
		} catch (Exception e) {
			String className = (clazz == null) ? "null" : clazz.getName();
			throw new ViewLayoutConfigurationException("failed to instantiate component from class " + className, e);
		}
	}

	public ComponentWrapper add(Class<? extends Component> clazz) {
		Component component = addComponent(clazz);
		ComponentWrapper wrapper = new ComponentWrapper(component);
		return wrapper;
	}

}
