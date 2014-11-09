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
package uk.q3c.util;

import java.util.List;

public class SourceTreeWrapper_BasicForest<S> implements SourceTreeWrapper<S> {

    private final BasicForest<S> forest;

    // private NodeModifier<S, T> nodeModifier = new DefaultNodeModifier<S, T>();

    public SourceTreeWrapper_BasicForest(BasicForest<S> forest) {
        super();
        this.forest = forest;
    }

    // public NodeModifier<S, T> getNodeModifier() {
    // return nodeModifier;
    // }

    // public void setNodeModifier(NodeModifier<S, T> nodeModifier) {
    // this.nodeModifier = nodeModifier;
    // }

    @Override
    public List<S> getRoots() {
        return forest.getRoots();
    }

    @Override
    public List<S> getChildren(S parentNode) {
        return forest.getChildren(parentNode);
    }

}
