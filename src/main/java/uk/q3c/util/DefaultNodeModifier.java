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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A default implementation for {@link NodeModifier} which assumes that the source and target nodes are the same type.
 * This would be used by {@link TreeCopy} where the nodes in the target tree are just references to nodes in the source
 * tree.
 *
 * @param <S> source node type
 * @param <T> target node type
 * @author David Sowerby
 *         27 May 2014
 */
public class DefaultNodeModifier<S, T> implements NodeModifier<S, T> {

    @SuppressWarnings("unchecked")
    @Override
    public S sourceNodeFor(@Nonnull T target) {
        checkNotNull(target);
        return (S) target;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(T parentNode, @Nonnull S sourceNode) {
        checkNotNull(sourceNode);
        return (T) sourceNode;
    }

    /**
     * Does nothing by default
     */
    @Override
    public void setLeaf(@Nonnull T targetNode) {

    }

    /**
     * Does nothing by default
     */
    @Override
    public void forceSetLeaf(@Nonnull T targetNode) {

    }

    /**
     * Does nothing by default
     */
    @Override
    public void setCaption(@Nonnull T targetNode, String caption) {
    }

    @Override
    public boolean attachOnCreate() {
        return false;
    }

    /**
     * Does nothing by default
     */
    @Override
    public void sortChildren(@Nullable T parentNode, @Nonnull Comparator<T> comparator) {

    }

}
