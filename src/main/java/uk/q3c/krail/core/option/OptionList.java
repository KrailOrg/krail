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

package uk.q3c.krail.core.option;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections15.ListUtils;

import javax.annotation.concurrent.Immutable;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Encapsulates a list for {@link Option} - the element class needs to be specified so that conversion to and from String can be performed
 * <p>
 * Created by David Sowerby on 05/08/15.
 */
@Immutable
public class OptionList<E> implements OptionCollection<E> {

    private final Class<E> elementClass;
    private final ImmutableList<E> list;

    public OptionList(Class<E> elementClass) {
        checkNotNull(elementClass);
        this.list = ImmutableList.of();
        this.elementClass = elementClass;
    }

    public OptionList(List<E> list, Class<E> elementClass) {
        checkNotNull(list);
        checkNotNull(elementClass);
        this.list = ImmutableList.copyOf(list);
        this.elementClass = elementClass;
    }

    @SafeVarargs
    public OptionList(Class<E> elementClass, E... elements) {
        checkNotNull(elementClass);
        this.list = ImmutableList.copyOf(elements);
        this.elementClass = elementClass;
    }

    public OptionList(OptionList<E> source) {
        checkNotNull(source);
        this.list = source.list;
        this.elementClass = source.elementClass;
    }

    public Class<E> getElementClass() {
        return elementClass;
    }

    public ImmutableList<E> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }


    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionList<?> that = (OptionList<?>) o;

        if (!elementClass.equals(that.elementClass)) return false;
        return ListUtils.isEqualList(list, that.list);

    }

    @Override
    public int hashCode() {
        int result = elementClass.hashCode();
        return 31 * result + list.hashCode();
    }
}
