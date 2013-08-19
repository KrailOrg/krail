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

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import uk.co.q3c.v7.base.view.template.DefaultViewConfig.Split;

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
		Deque<VerticalSplitPanel> q = buildSplitterQueue(config.splitCount());
		// VerticalSplitPanel firstVsp = q.peek();
		// boolean firstComponent = firstVsp.getFirstComponent() == null;
		boolean firstComponent = true;
		Iterator<Split> spliterator = config.splitIterator();
		Split currentSplit = spliterator.next();

		int i = 0;
		for (Component c : components) {
			if ((currentSplit != null) && (currentSplit.section2 == i)) {
				firstComponent = !firstComponent;
				// we've done both first and second components
				if (firstComponent) {
					q.pop();
				}
				if (spliterator.hasNext()) {
					currentSplit = spliterator.next();
				} else {
					currentSplit = null;
				}
			}

			VerticalSplitPanel vsp = q.peek();

			// when there is an even number of splits, the second component of the root is left empty, and is the last
			// section to be filled
			if ((q.size() == 1) && (vsp.getFirstComponent() instanceof VerticalSplitPanel)) {
				firstComponent = false;
			}
			Component currentContent = (firstComponent) ? vsp.getFirstComponent() : vsp.getSecondComponent();
			if (currentContent == null) {
				currentContent = c;
			} else {
				if (currentContent instanceof VerticalLayout) {
					((VerticalLayout) currentContent).addComponent(c);
				} else {
					VerticalLayout vl = new VerticalLayout();
					vl.addComponent(currentContent);
					vl.addComponent(c);
					currentContent = vl;

				}
			}
			if (firstComponent) {
				vsp.setFirstComponent(currentContent);
			} else {
				vsp.setSecondComponent(currentContent);
			}
			i++;
		}

	}

	/**
	 * When there are no splits, just use a VerticalLayout
	 */
	private void assembleNoSplits() {
		VerticalLayout vl = new VerticalLayout();
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

	protected LinkedList<VerticalSplitPanel> buildSplitterQueue(int numberOfSplitters) {
		LinkedList<VerticalSplitPanel> oldQ = new LinkedList<>();
		LinkedList<VerticalSplitPanel> newQ = new LinkedList<>();
		// seed the old q
		splitCount = -1;
		VerticalSplitPanel splitter = newSplitter();
		oldQ.add(splitter);
		boolean completed = false;
		while (splitCount < numberOfSplitters - 1) {
			newQ = new LinkedList<>();
			while (oldQ.size() > 0) {
				VerticalSplitPanel parentVsp = oldQ.peek();
				splitter = newSplitter();
				parentVsp.setFirstComponent(splitter);
				newQ.add(splitter);
				if (splitCount >= (numberOfSplitters - 1)) {
					newQ.addAll(oldQ);
					qstat(newQ);
					completed = true;
					break;
				}
				splitter = newSplitter();
				parentVsp.setSecondComponent(splitter);
				oldQ.pop();
				newQ.add(splitter);
				if (splitCount >= (numberOfSplitters - 1)) {
					newQ.addAll(oldQ);
					qstat(newQ);
					completed = true;
					break;
				}
				qstat(newQ);
			}
			qstat(newQ);
			oldQ = newQ;
			qstat(oldQ);
		}
		// If any from the oldq not completely filled, they are transfered
		// also covers situation where fill completes exactly, and newq has been moved to oldq
		if (!completed) {
			newQ.addAll(oldQ);
		}
		qstat(newQ);
		return newQ;

	}

	private VerticalSplitPanel newSplitter() {
		VerticalSplitPanel splitter = new VerticalSplitPanel();
		splitCount++;
		splitter.setId("vsp" + splitCount);
		return splitter;
	}

	private void qstat(LinkedList<VerticalSplitPanel> splitterQ) {
		StringBuilder buf = new StringBuilder();
		for (VerticalSplitPanel vsp : splitterQ) {
			buf.append(vsp.getId() + ",");
		}
		System.out.println(buf.toString());
	}
}
