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
import uk.q3c.krail.testbench.KrailTestBenchTestCase;

/**
 * Created by david on 03/10/14.
 */
public class PageObject {

    private static Logger log = LoggerFactory.getLogger(PageObject.class);
    protected KrailTestBenchTestCase parentCase;

    /**
     * Initialises the PageObject with a reference to the parent test case, so that the PageObject can access a number
     * of variables from the parent, for example: drivers, baseUrl,
     * application appContext.
     * <p/>
     * <p/>
     * Note that all calls requiring eventual access to a WebDriver should be made via the parentCase, so that the
     * correct driver is acted on.
     *
     * @param parentCase
     */
    public PageObject(KrailTestBenchTestCase parentCase) {
        this.parentCase = parentCase;
    }

    public void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            log.error("Sleep was interrupted");
        }
    }

    public <E extends AbstractComponentElement> E element(Class<E> elementClass, Optional<?> qualifier,
                                                          Class<?>... componentClasses) {

        PageElement<E> pageElement = new PageElement(parentCase, elementClass, qualifier, componentClasses);
        return pageElement.getElement();
    }

    public <E extends AbstractComponentElement> E element(Class<E> elementClass, String id) {

        PageElement<E> pageElement = new PageElement(parentCase, elementClass, id);
        return pageElement.getElement();
    }
}
