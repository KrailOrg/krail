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
package uk.q3c.krail.core.vaadin;


import uk.q3c.util.clazz.DefaultClassNameUtils;

import java.util.Optional;

import static com.google.common.base.Preconditions.*;

/**
 * Utility class used to standardise id setting (setId methods in Components).
 * <p/>
 * The qualifier is just a way of identifying one of several of the same type of component in the same parent
 * component.
 * (for example, the 'Cancel' button from 'Save' and 'Cancel' buttons in a dialog).
 * <p/>
 * The components list is a notional hierarchy of components (it can be anything that makes sense in your
 * environment, this is just an identifier). Although it is generally instances of components you would use here,
 * you may wish to use other things (for example KrailView implementations) which are not components in their own
 * right, therefore the method takes Object rather than Component parameters
 *
 * @author David Sowerby 15 Sep 2013
 */
public class ID {


    public static String getId(Optional<?> qualifier, Object... components) {
        checkNotNull(qualifier);
        Class<?>[] classes = new Class<?>[components.length];
        for (int i = 0; i < components.length; i++) {
            classes[i] = components[i].getClass();
        }
        return getIdc(qualifier, classes);
    }


    public static String getIdc(Optional<?> qualifier, Class<?>... componentClasses) {
        checkNotNull(qualifier);
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        DefaultClassNameUtils classNameUtil = new DefaultClassNameUtils();
        for (Class<?> c : componentClasses) {
            if (!first) {
                buf.append('-');
            } else {
                first = false;
            }
            //https://github.com/davidsowerby/krail/issues/383
            //enhanced classes mess up the class name with $$Enhancer
            buf.append(classNameUtil.simpleClassNameEnhanceRemoved(c));
        }
        if (qualifier.isPresent()) {
            buf.append('-');
            buf.append(qualifier.get());
        }
        return buf.toString();
    }


}
