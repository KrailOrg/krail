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

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

public interface ViewConfig {

	/**
	 * In most layouts there is one component which takes up all the remaining space. This is usually achieved by a call
	 * to {@link AbstractOrderedLayout#setExpandRatio(Component, float)}, but the implementation of this method
	 * determines which component to expand from the index provided
	 */
	void setExpandedItem(int index);

	void addSplit(int section1, int section2);

	void removeSplit(int section1, int section2);

	boolean hasSplit(int section1, int section2);

	Iterator<Split> splitIterator();

}
