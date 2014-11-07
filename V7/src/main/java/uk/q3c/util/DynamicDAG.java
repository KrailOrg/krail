/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.util;

import java.util.Collection;
import java.util.Stack;

public class DynamicDAG<V> extends BasicForest<V> {

    public DynamicDAG() {
        super();
    }

    @Override
    public void addChild(V parentNode, V childNode) {
        if (!detectCycle(parentNode, childNode)) {
            super.addChild(parentNode, childNode);
        } else {
            throw new CycleDetectedException();
        }
    }

    /**
     * Checks the proposed connection between parent and child nodes, and returns true if a cycle would be created by
     * adding the child to the parent, or false if not
     *
     * @param parentNode
     * @param childNode
     *
     * @return
     */
    protected boolean detectCycle(V parentNode, V childNode) {
        if (parentNode == childNode) {
            return true;
        }
        Stack<V> stack = new Stack<>();
        stack.push(parentNode);
        while (!stack.isEmpty()) {
            V node = stack.pop();
            Collection<V> predecessors = getGraph().getPredecessors(node);
            if (predecessors != null) {
                for (V pred : predecessors) {
                    if (pred == childNode) {
                        return true;
                    }
                }
                stack.addAll(predecessors);
            }
        }
        return false;

    }

}
