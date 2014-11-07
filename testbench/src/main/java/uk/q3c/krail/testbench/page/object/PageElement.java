/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.testbench.page.object;

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.AbstractComponentElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.testbench.V7TestBenchTestCase;
import uk.q3c.util.ID;

/**
 * Created by david on 03/10/14.
 */
public class PageElement<E extends AbstractComponentElement> {

    private static Logger log = LoggerFactory.getLogger(PageElement.class);

    private final String id;
    protected V7TestBenchTestCase parentCase;
    private Class<E> elementClass;


    public PageElement(V7TestBenchTestCase parentCase, Class<E> elementClass, Optional<?> qualifier,
                       Class<?>... componentClasses) {
        this.parentCase = parentCase;
        this.id = ID.getIdc(qualifier, componentClasses);
        this.elementClass = elementClass;
    }

    public PageElement(V7TestBenchTestCase parentCase, Class<E> elementClass, String id) {
        this.parentCase = parentCase;
        this.id = id;
        this.elementClass = elementClass;
    }

    public void click() {
        getElement().click();
    }

    public E getElement() {
        return parentCase.element(elementClass, id);
    }

    public String getText() {
        return getElement().getText();
    }

    /**
     * Set text for the element, but will add it to text that is already there. If you want to replace the existing
     * text, you will need to call {@link #clear()} first.
     *
     * @param text
     */
    public void setText(String text) {
        getElement().sendKeys(text);
    }

    public String getCaption() {
        return getElement().getCaption();
    }

    public void clear() {
        getElement().clear();
    }

    protected void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            log.error("Sleep was interrupted");
        }
    }


}
