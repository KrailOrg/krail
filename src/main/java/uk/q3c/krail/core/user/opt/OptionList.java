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

package uk.q3c.krail.core.user.opt;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Encapsulates a list for {@link Option} - the element class needs to be specified so that conversion to and from String can be performed
 * <p>
 * Created by David Sowerby on 05/08/15.
 */
public class OptionList<E> {

    private final Class<E> elementClass;
    protected ImmutableList<E> list;

    public OptionList(Class<E> elementClass) {
        this.list = ImmutableList.of();
        this.elementClass = elementClass;
    }

    public OptionList(List<E> list, Class<E> elementClass) {
        this.list = ImmutableList.copyOf(list);
        this.elementClass = elementClass;
    }

    /**
     * The list cannot be empty for this constructor to work
     *
     * @param list
     *         to set values of this list
     */
    public OptionList(List<E> list) {
        if (list.isEmpty()) {
            throw new OptionException("This constructor requires the list parameter to have at least one element");
        }
        this.list = ImmutableList.copyOf(list);
        final E e = list.get(0);
        this.elementClass = (Class<E>) e.getClass();
    }

    /**
     * The list cannot be empty for this constructor to work
     *
     * @param elements
     *         to set values of this list
     */
    @SafeVarargs
    public OptionList(E... elements) {
        if (elements.length == 0) {
            throw new OptionException("This constructor requires the list parameter to have at least one element");
        }
        this.list = ImmutableList.copyOf(elements);
        final E e = elements[0];
        //noinspection unchecked
        this.elementClass = (Class<E>) e.getClass();
    }

    @SafeVarargs
    public OptionList(Class<E> elementClass, E... elements) {
        this.list = ImmutableList.copyOf(elements);
        this.elementClass = elementClass;
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


}
