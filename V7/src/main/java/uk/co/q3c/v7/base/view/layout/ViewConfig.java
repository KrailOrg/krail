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

import java.util.Iterator;

import uk.co.q3c.v7.base.view.layout.DefaultViewConfig.Split;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * Holds various configuration settings to be used by a {@link ViewLayout}. 'Splits' in user interface terms, are
 * instances of either {@link VerticalSplitPanel} or {@link HorizontalSplitPanel}, and are defined as being between two
 * component indexes. Most calls return 'this' to enable cascaded calls:
 * <p>
 * addSplit(1,2).addSplit(2,3);
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
	ViewConfig addSplit(int index1, int index2);

	/**
	 * Remove the previously defined splitter between the components at {@code index1} and {@code index2}
	 * 
	 * @param index1
	 * @param index2
	 * @return
	 */
	ViewConfig removeSplit(int index1, int index2);

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
	 * Sets width and height of components to the default values held by this instance, unless defaults have been
	 * disabled
	 * 
	 * @param component
	 */
	void setDefaults(AbstractComponent component);

	/**
	 * Do not set any default widths
	 * 
	 * @return
	 */
	ViewConfig noWidth();

	/**
	 * Do not set any default heights
	 * 
	 * @return
	 */
	ViewConfig noHeight();

	/**
	 * Do not set any default widths or heights
	 * 
	 * @return
	 */
	ViewConfig noSize();

	/**
	 * Enable the setting of default widths
	 * 
	 * @return
	 */
	ViewConfig doWidth();

	/**
	 * Enable the setting of default heights
	 * 
	 * @return
	 */
	ViewConfig doHeight();

	/**
	 * Enable the setting of default widths and heights
	 * 
	 * @return
	 */
	ViewConfig doSize();

	/**
	 * The Unit to use in setting component widths. If not set explicitly, it will be {@link Unit#PIXELS}
	 * 
	 * @param widthUnit
	 * @return
	 */
	ViewConfig widthUnit(Unit widthUnit);

	/**
	 * The Unit to use in setting component heights. If not set explicitly, it will be {@link Unit#PIXELS}
	 * 
	 * @param widthUnit
	 * @return
	 */
	ViewConfig heightUnit(Unit heightUnit);

	/**
	 * The value of width to set components to, combined with the default unit type {@link Unit#PIXELS}, or the unit
	 * type set via {@link #widthUnit(Unit)}. Has no effect if setting of widths is disabled via {@link #noWidth()}
	 * 
	 * @param width
	 * @return
	 */
	ViewConfig width(float width);

	/**
	 * The value of height to set components to, combined with the default unit type {@link Unit#PIXELS}, or the unit
	 * type set via {@link #heightUnit(Unit)}. Has no effect if setting of heights is disabled via {@link #noHeight()}
	 * 
	 * @param height
	 * @return
	 */
	ViewConfig height(float height);

	/**
	 * The Unit to use in setting component widths and heights. If not set explicitly, it will be {@link Unit#PIXELS}.
	 * The same as calling {@link #widthUnit(Unit)} and {@link #heightUnit(Unit)}
	 * 
	 * @param widthUnit
	 * @return
	 */
	ViewConfig sizeUnit(Unit widthUnit, Unit heightUnit);

	/**
	 * The Unit to use in setting component widths and heights, using the same value for each. If not set explicitly, it
	 * will be {@link Unit#PIXELS}. The same as calling {@link #widthUnit(Unit)} and {@link #heightUnit(Unit)}
	 * 
	 * @param widthUnit
	 * @return
	 */
	ViewConfig sizeUnit(Unit unit);

	/**
	 * The value of width and height to set components to, combined with their associated default unit type
	 * {@link Unit#PIXELS}, or the unit type set via {@link #widthUnit(Unit)} and {@link #heightUnit(Unit)}. Setting of
	 * widths or heights can be disabled via {@link #noWidth()} or {@link #noHeight()}
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	ViewConfig size(float width, float height);

	Unit getWidthUnit();

	Unit getHeightUnit();

	float getHeight();

	float getWidth();

	boolean isWidthEnabled();

	boolean isHeightEnabled();

}
