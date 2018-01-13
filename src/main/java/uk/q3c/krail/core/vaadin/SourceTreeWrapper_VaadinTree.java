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

import com.vaadin.ui.Tree;
import uk.q3c.util.forest.SourceTreeWrapper;

import java.util.List;

public class SourceTreeWrapper_VaadinTree<S> implements SourceTreeWrapper<S> {

    private final Tree<S> tree;

    public SourceTreeWrapper_VaadinTree(Tree<S> tree) {
        super();
        this.tree = tree;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<S> getRoots() {
        return tree.getTreeData().getRootItems();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<S> getChildren(S parentNode) {
        return tree.getTreeData().getChildren(parentNode);
    }

}
