/*
 * Copyright (C) 2014 David Sowerby
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
package uk.q3c.krail.core.vaadin;

import com.vaadin.v7.ui.Tree;
import uk.q3c.util.forest.DefaultNodeModifier;
import uk.q3c.util.forest.TargetTreeWrapperBase;
import uk.q3c.util.forest.TreeCopyException;

import java.util.Comparator;

public class TargetTreeWrapper_VaadinTree<S, T> extends TargetTreeWrapperBase<S, T> {

    private final Tree tree;

    public TargetTreeWrapper_VaadinTree(Tree tree) {
        super();
        this.tree = tree;
        nodeModifier = new DefaultNodeModifier<S, T>();
    }

    @Override
    public T createNode(T parentNode, S sourceChildNode) {
        T newTargetNode = super.createNode(parentNode, sourceChildNode);
        tree.setItemCaption(newTargetNode, getCaptionReader().getCaption(sourceChildNode));
        return newTargetNode;
    }

    /**
     * Not supported in this implementation
     */
    @Override
    public void sortChildren(T parentNode, Comparator<T> comparator) {
        throw new TreeCopyException("Sort cannot be performed after child target nodes added to parent in this " +
                "implementation");
    }

    @Override
    public void addChild(T parentNode, T childNode) {
        tree.addItem(childNode);
        if (parentNode != null) {
            if (tree.getItem(parentNode) == null) {
                tree.addItem(parentNode);
            }
        }
        tree.setParent(childNode, parentNode);

    }

}
