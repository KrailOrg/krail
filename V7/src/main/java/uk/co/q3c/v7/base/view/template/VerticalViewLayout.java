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
import java.util.LinkedList;

import uk.co.q3c.v7.base.view.template.DefaultViewConfig.Split;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSplitPanel;
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
	private int splitCount = 0;

	protected VerticalViewLayout() {
		super();

	}

	@Override
	public void assemble(ViewConfig config) {
		if (config.splitCount() == 0) {
			assembleNoSplits();
			return;
		}
		validateSplits(config);
		LinkedList<AbstractSplitPanel> q = buildSplitterQueue(config.splitCount());
		LinkedList<Integer> populations = buildPopulations(config);

		int c = 0;
		for (Integer i : populations) {
			if (i == 1) {
				if (q.peek().getFirstComponent() == null) {
					q.peek().setFirstComponent(components.get(c));
				} else {
					q.peek().setSecondComponent(components.get(c));
					q.pop();
				}
				c++;
			} else {
				AbstractOrderedLayout vl = newVaadinLayout();
				for (int k = 1; k <= i; k++) {
					vl.addComponent(components.get(c));
					c++;
				}
				if (q.peek().getFirstComponent() == null) {
					q.peek().setFirstComponent(vl);
				} else {
					q.peek().setSecondComponent(vl);
					q.pop();
				}
			}
		}

	}

	protected AbstractOrderedLayout newVaadinLayout() {
		return new VerticalLayout();
	}

	protected void validateSplits(ViewConfig config) {
		Iterator<Split> spliterator = config.splitIterator();
		while (spliterator.hasNext()) {
			Split split = spliterator.next();
			int diff = Math.abs(split.section1 - split.section2);
			if (diff > 1) {
				throw new ViewLayoutConfigurationException("A splitter can only be between adjacent components");
			}
		}
	}

	/**
	 * When there are no splits, just use a VerticalLayout
	 */
	private void assembleNoSplits() {
		AbstractOrderedLayout vl = newVaadinLayout();
		for (Component c : components) {
			vl.addComponent(c);
		}

		layoutRoot = vl;
	}

	@Override
	public ViewConfig defaultConfig() {
		DefaultViewConfig config = new DefaultViewConfig();
		return config;
	}

	protected LinkedList<Integer> buildPopulations(ViewConfig config) {
		LinkedList<Integer> populations = new LinkedList<>();
		Iterator<Split> spliterator = config.splitIterator();
		int marker = 0;
		while (spliterator.hasNext()) {
			Split split = spliterator.next();
			int pop = split.section2 - marker;
			populations.add(pop);
			marker = split.section2;
		}
		int remainder = components.size() - marker;
		populations.add(remainder);
		return populations;
	}

	protected LinkedList<AbstractSplitPanel> buildSplitterQueue(int numberOfSplitters) {
		LinkedList<AbstractSplitPanel> oldQ = new LinkedList<>();
		LinkedList<AbstractSplitPanel> newQ = new LinkedList<>();
		// seed the old q
		splitCount = -1;
		AbstractSplitPanel splitter = newSplitter();
		layoutRoot = splitter;
		oldQ.add(splitter);
		boolean completed = false;
		while (splitCount < numberOfSplitters - 1) {
			newQ = new LinkedList<>();
			while (oldQ.size() > 0) {
				AbstractSplitPanel parentVsp = oldQ.peek();
				splitter = newSplitter();
				parentVsp.setFirstComponent(splitter);
				newQ.add(splitter);
				if (splitCount >= (numberOfSplitters - 1)) {
					newQ.addAll(oldQ);
					completed = true;
					break;
				}
				splitter = newSplitter();
				parentVsp.setSecondComponent(splitter);
				oldQ.pop();
				newQ.add(splitter);
				if (splitCount >= (numberOfSplitters - 1)) {
					newQ.addAll(oldQ);
					completed = true;
					break;
				}
			}
			oldQ = newQ;
		}
		// If any from the oldq not completely filled, they are transfered
		// also covers situation where fill completes exactly, and newq has been moved to oldq
		if (!completed) {
			newQ.addAll(oldQ);
		}
		return newQ;

	}

	private AbstractSplitPanel newSplitter() {
		AbstractSplitPanel splitter = newVaadinSplitPanel();
		splitCount++;
		splitter.setId("vsp" + splitCount);
		return splitter;
	}

	protected AbstractSplitPanel newVaadinSplitPanel() {
		return new VerticalSplitPanel();
	}

}
