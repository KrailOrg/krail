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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.q3c.v7.base.view.layout.DefaultViewConfig.Split;
import uk.co.q3c.v7.i18n.I18NTranslator;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

public abstract class ViewLayoutBase implements ViewLayout {

	protected Component layoutRoot;
	protected ViewConfig config;

	protected final List<AbstractComponent> components;
	/**
	 * Sorted set required as the splits need to be processed in order
	 */
	protected final SortedSet<Split> validSplits = new TreeSet<>();

	protected ViewLayoutBase() {
		super();
		components = new ArrayList<>();

	}

	@Override
	public void localeChange(I18NTranslator translator) {
		translator.translate(this);
	}

	@Override
	public void assemble() {
		validateSplits();
		doAssemble();

	}

	/**
	 * Override this to assemble components into layouts. See {@link VerticalViewLayout#doAssemble()} for an example
	 */
	protected abstract void doAssemble();

	@Override
	public int transferComponentsFrom(ViewLayout source) {

		throw new RuntimeException("not yet implemented");
	}

	@Override
	public List<AbstractComponent> orderedComponents() {
		return new ArrayList<>(components);
	}

	@Override
	public void setComponent(int index, AbstractComponent component) {
		//
		throw new RuntimeException("not yet implemented");
	}

	@Override
	public void addComponent(AbstractComponent component) {
		components.add(component);
	}

	@Override
	public Component getLayoutRoot() {
		return layoutRoot;
	}

	@Override
	public ViewConfig defaultConfig() {
		DefaultViewConfig config = new DefaultViewConfig();
		config.widthUnit(Unit.PERCENTAGE).width(100).noHeight();
		return config;
	}

	@Override
	public void validateSplits() {
		Iterator<Split> spliterator = config.splitIterator();
		validSplits.clear();
		while (spliterator.hasNext()) {
			Split split = spliterator.next();
			if (isValidSplit(split)) {
				validSplits.add(split);
			}

		}
	}

	@Override
	public int validSplitCount() {
		return validSplits.size();
	}

	@Override
	public ViewConfig getConfig() {
		return config;
	}

	@Override
	public void setConfig(ViewConfig config) {
		this.config = config;
	}
}
