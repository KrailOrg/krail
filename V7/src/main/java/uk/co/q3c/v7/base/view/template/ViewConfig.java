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

import java.util.Iterator;

import uk.co.q3c.v7.base.view.template.DefaultViewConfig.Split;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * Holds various configuration settings to be used by a {@link ViewLayout}. 'Splits' in user interface terms, are
 * instances of either {@link VerticalSplitPanel} or {@link HorizontalSplitPanel}, and are defined as being between two
 * component indexes.
 * 
 * @author David Sowerby 20 Aug 2013
 * 
 */
public interface ViewConfig {

	/**
	 * In most layouts there is one component which takes up all the remaining space. This is usually achieved by a call
	 * to {@link AbstractOrderedLayout#setExpandRatio(Component, float)}, but the implementation of this method
	 * determines which component to expand from the index provided
	 */
	void setExpandedItem(int index);

	/**
	 * Add a split (in user interface terms, this is either a {@link VerticalSplitPanel} or {@link HorizontalSplitPanel}
	 * ) between the components at the indices specified
	 * 
	 * @param section1
	 * @param section2
	 */
	void addSplit(int index1, int index2);

	/**
	 * Remove the previously defined splitter between the components at {@code index1} and {@code index2}
	 * 
	 * @param index1
	 * @param index2
	 */
	void removeSplit(int index1, int index2);

	/**
	 * Returns true if there is a split defined splitter between the components at {@code index1} and {@code index2}
	 * 
	 * @param index1
	 * @param index2
	 */
	boolean hasSplit(int index1, int index2);

	Iterator<Split> splitIterator();

	/**
	 * The number of splits defined. Use with care, however, as not all of the splits may be valid for a given layout.
	 * Consider using {@link ViewLayout#validSplitCount()} instead
	 * 
	 * @return
	 */
	int splitCount();

	/**
	 * If true, it is expected that default size values will be assigned to all components.
	 * 
	 * @return
	 */
	boolean isDefaultsEnabled();

	/**
	 * Enables the setting of default size values
	 */
	void enableDefaults();

	/**
	 * Disables the setting of default size values
	 */
	void disableDefaults();

	float getDefaultWidth();

	float getDefaultHeight();

	Unit getDefaultUnit();

}
