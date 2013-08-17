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

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * This is one of the few {@link ViewLayout} implementations provided within the V7 base library. It is intended that
 * others will be provided in a companion library. This implementation provides the standard {@link VerticalLayout}
 * functionality, plus it allows the specification of a "splitter" at any one or more places between components.
 * 
 * @author David Sowerby 29 Mar 2013
 * 
 */
public class VerticalViewLayout extends ViewLayoutBase {

	protected VerticalViewLayout() {
		super();

	}

	@Override
	public void assemble(ViewConfig config) {
		Component currentContainer = null;

		Iterator<Split> splitIterator = config.splitIterator();
		Iterator<AbstractComponent> componentIterator = components.iterator();

		Split currentSplit = splitIterator.hasNext() ? splitIterator.next() : null;
		int c = 0;

		while (componentIterator.hasNext()) {
			AbstractComponent component = componentIterator.next();
			// this relies on all splits being valid for this layout
			if ((currentSplit != null) && (currentSplit.section1 == c)) {
				// create splitter and put current into left/top split
				VerticalSplitPanel vsp = new VerticalSplitPanel();
				vsp.setFirstComponent(component);
				if (currentContainer == null) {
					currentContainer = vsp;
				} else {
					if (currentContainer instanceof VerticalLayout) {
						((VerticalLayout) currentContainer).addComponent(vsp);
					} else {
						((VerticalSplitPanel) currentContainer).setSecondComponent(vsp);
					}
				}
				if (layoutRoot == null) {
					layoutRoot = vsp;
				}

				currentContainer = vsp;
				currentSplit = splitIterator.hasNext() ? splitIterator.next() : null;

			} else {
				// if not already a vl create it
				if (currentContainer == null) {
					currentContainer = new VerticalLayout();
					if (layoutRoot == null) {
						layoutRoot = currentContainer;
					}
				}
				if (currentContainer instanceof VerticalLayout) {
					((VerticalLayout) currentContainer).addComponent(component);
				} else {
					VerticalSplitPanel vsp = (VerticalSplitPanel) currentContainer;
					Component vsp2 = vsp.getSecondComponent();
					if (vsp2 == null) {
						vsp.setSecondComponent(component);
					} else {
						if (vsp2 instanceof VerticalLayout) {
							((VerticalLayout) vsp2).addComponent(component);
						} else {
							VerticalLayout vl = new VerticalLayout();
							vsp.setSecondComponent(vl);
							vl.addComponent(vsp2);
							vl.addComponent(component);
							currentContainer = vl;
						}
					}
				}
				// put into vl

			}
			c++;
		}

		// for (Component c : components) {
		// vl.addComponent(c);
		// }
		// layoutRoot = vl;
	}

	@Override
	public ViewConfig defaultConfig() {
		DefaultViewConfig config = new DefaultViewConfig();
		return config;
	}

}
