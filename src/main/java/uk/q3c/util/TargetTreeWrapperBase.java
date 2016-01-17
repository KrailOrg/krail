/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.util;

import java.io.Serializable;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class TargetTreeWrapperBase<S, T> implements TargetTreeWrapper<S, T>, Serializable {
    protected NodeModifier<S, T> nodeModifier;
    private CaptionReader<S> captionReader;

    @Override
    public CaptionReader<S> getCaptionReader() {
        return captionReader;
    }

    @Override
    public void setCaptionReader(CaptionReader<S> captionReader) {
        this.captionReader = captionReader;
    }

    /**
     * Delegates to the NodeModifer if there is one, otherwise does nothing
     */
    @Override
    public void setLeaf(T node, boolean isLeaf) {
        if (nodeModifier != null) {
            nodeModifier.setLeaf(node, isLeaf);
        }
    }

    /**
     * Delegates to the NodeModifer if there is one, otherwise attempts to cast the {@code sourceChildNode} as the
     * target (which will cause a TreeCopyException if target and source types are incompatible)
     */
    @Override
    public T createNode(T parentNode, S sourceChildNode) {
        checkNotNull(sourceChildNode);
        checkNotNull(nodeModifier);
        return getNodeModifier().create(parentNode, sourceChildNode);
    }

    @Override
    public NodeModifier<S, T> getNodeModifier() {
        return nodeModifier;
    }

    @Override
    public void setNodeModifier(NodeModifier<S, T> nodeModifier) {
        this.nodeModifier = nodeModifier;
    }

    @Override
    public void sortChildren(T parentNode, Comparator<T> comparator) {
        nodeModifier.sortChildren(parentNode, comparator);
    }

}
