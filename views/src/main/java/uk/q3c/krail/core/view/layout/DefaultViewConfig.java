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
package uk.q3c.krail.core.view.layout;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class DefaultViewConfig implements ViewConfig {

    private final SortedSet<Split> splits;
    private int expandedItem;
    private float height = -1;
    private boolean heightEnabled = true;
    private Unit heightUnit = Unit.PIXELS;
    private float width = -1;
    private boolean widthEnabled = true;
    private Unit widthUnit = Unit.PIXELS;

    public DefaultViewConfig() {
        splits = new TreeSet<>();
    }

    /**
     * Adds a split, and ensures that lowest section number is always first
     *
     * @param section1
     * @param section2
     *
     * @return
     */
    @Override
    public ViewConfig addSplit(int section1, int section2) {
        splits.add(createSplit(section1, section2));
        return this;
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

    @Override
    public ViewConfig removeSplit(int section1, int section2) {
        Split split = createSplit(section1, section2);
        splits.remove(split);
        return this;
    }

    @Override
    public boolean hasSplit(int section1, int section2) {
        Split split = createSplit(section1, section2);
        return splits.contains(split);
    }

    public SortedSet<Split> getSplits() {
        return splits;
    }

    @Override
    public Iterator<Split> splitIterator() {
        return splits.iterator();
    }

    public int getExpandedItem() {
        return expandedItem;
    }

    @Override
    public void setExpandedItem(int index) {
        this.expandedItem = index;
    }

    @Override
    public int splitCount() {
        return splits.size();
    }

    @Override
    public void setDefaults(Component component) {
        if (widthEnabled) {
            component.setWidth(width, widthUnit);
        }
        if (heightEnabled) {
            component.setHeight(height, heightUnit);
        }
    }

    @Override
    public ViewConfig noWidth() {
        widthEnabled = false;
        return this;
    }

    @Override
    public ViewConfig noHeight() {
        heightEnabled = false;
        return this;
    }

    @Override
    public ViewConfig doWidth() {
        widthEnabled = true;
        return this;
    }

    @Override
    public ViewConfig doHeight() {
        heightEnabled = true;
        return this;
    }

    @Override
    public ViewConfig width(float width) {
        this.width = width;
        return this;
    }

    @Override
    public ViewConfig height(float height) {
        this.height = height;
        return this;
    }

    @Override
    public ViewConfig widthUnit(Unit widthUnit) {
        this.widthUnit = widthUnit;
        return this;
    }

    @Override
    public ViewConfig heightUnit(Unit heightUnit) {
        this.heightUnit = heightUnit;
        return this;
    }

    @Override
    public ViewConfig noSize() {
        widthEnabled = false;
        heightEnabled = false;
        return this;
    }

    @Override
    public ViewConfig doSize() {
        widthEnabled = true;
        heightEnabled = true;
        return this;
    }

    @Override
    public ViewConfig sizeUnit(Unit widthUnit, Unit heightUnit) {
        this.widthUnit = widthUnit;
        this.heightUnit = heightUnit;
        return this;
    }

    @Override
    public ViewConfig sizeUnit(Unit unit) {
        this.widthUnit = unit;
        this.heightUnit = unit;
        return this;
    }

    @Override
    public ViewConfig size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public Unit getWidthUnit() {
        return widthUnit;
    }

    @Override
    public Unit getHeightUnit() {
        return heightUnit;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public boolean isWidthEnabled() {
        return widthEnabled;
    }

    @Override
    public boolean isHeightEnabled() {
        return heightEnabled;
    }

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
            if (this == otherSplit) return true;
            if (otherSplit == null) return false;
            if (getClass() != otherSplit.getClass()) return false;
            Split other = (Split) otherSplit;
            if (section1 != other.section1) return false;
            if (section2 != other.section2) return false;
            return true;
        }

        @Override
        public int compareTo(Split other) {
            return Integer.compare(this.composite, other.composite);
        }

    }
}
