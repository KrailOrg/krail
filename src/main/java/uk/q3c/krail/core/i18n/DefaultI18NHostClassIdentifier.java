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

package uk.q3c.krail.core.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of {@link I18NHostClassIdentifier} identifies classes enhanced by Guice AOP and returns the origin, un-enhanced class.
 * <p>
 * Created by David Sowerby on 10/05/15.
 */
public class DefaultI18NHostClassIdentifier implements I18NHostClassIdentifier {
    private static Logger log = LoggerFactory.getLogger(DefaultI18NHostClassIdentifier.class);

    @Override
    public Class<?> getOriginalClassFor(Object target) {
        Class clazz = target.getClass();
        if (clazz.getName()
                 .contains("$$EnhancerByGuice$$")) {
            log.debug("Guice AOP enhanced class detected, using superclass to read annotations");
            return clazz.getSuperclass();
        } else {
            if (clazz.getName()
                     .contains("$$")) {
                log.warn("The I18NProcessor has been invoked on a proxy object or object which has been enhanced by something other than Guice AOP.  This " +
                        "means" +
                        " it will not be able to detect any annotated fields.\n" + "" +
                        "Possible solutions are either to separate the annotated fields from the AOP intercepted methods, perhaps by injecting a worker " +
                        "object,\n" +
                        " or to directly code the equivalent of the AOP interception.\n" +
                        "Better still, you may be able to provide a different implementation of this class, which knows how to identify the original, " +
                        "un-enhanced, class");
            }
        }
        return clazz;
    }
}
