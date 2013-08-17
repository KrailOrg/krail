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
import java.util.SortedSet;
import java.util.TreeSet;

public class DefaultViewConfig implements ViewConfig {

	private final SortedSet<Split> splits;
	private int expandedItem;

	public static class Split implements Comparable<Split> {
		int section1;
		int section2;
		int composite;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + section1;
			result = prime * result + section2;
			return result;
		}

		@Override
		public boolean equals(Object otherSplit) {
			if (this == otherSplit)
				return true;
			if (otherSplit == null)
				return false;
			if (getClass() != otherSplit.getClass())
				return false;
			Split other = (Split) otherSplit;
			if (section1 != other.section1)
				return false;
			if (section2 != other.section2)
				return false;
			return true;
		}

		@Override
		public int compareTo(Split other) {
			return Integer.compare(this.composite, other.composite);
		}

	}

	public DefaultViewConfig() {
		splits = new TreeSet<>();
	}

	/**
	 * Adds a split, and ensures that lowest section number is always first
	 * 
	 * @param section1
	 * @param section2
	 */
	@Override
	public void addSplit(int section1, int section2) {
		splits.add(createSplit(section1, section2));
	}

	@Override
	public void removeSplit(int section1, int section2) {
		Split split = createSplit(section1, section2);
		splits.remove(split);
	}

	@Override
	public boolean hasSplit(int section1, int section2) {
		Split split = createSplit(section1, section2);
		return splits.contains(split);
	}

	private Split createSplit(int section1, int section2) {
		Split split = new Split();
		if (section2 > section1) {
			split.section1 = section1;
			split.section2 = section2;
		} else {
			split.section1 = section2;
			split.section2 = section1;
		}
		// composite value to enable sort splits in ascending order, where section1
		// is most significant
		// if we ever get more than 100,000 sections this will break!

		split.composite = (section1 * 100000) + section2;
		return split;
	}

	public SortedSet<Split> getSplits() {
		return splits;
	}

	@Override
	public void setExpandedItem(int index) {
		this.expandedItem = index;
	}

	@Override
	public Iterator<Split> splitIterator() {
		return splits.iterator();
	}

	public int getExpandedItem() {
		return expandedItem;
	}

}
