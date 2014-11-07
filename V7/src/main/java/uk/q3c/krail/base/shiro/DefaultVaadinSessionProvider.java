/*
 * Copyright (C) 2013 David Sowerby
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
package uk.q3c.krail.base.shiro;

import com.vaadin.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Sowerby 15 Sep 2013
 * @see VaadinSessionProvider
 */
public class DefaultVaadinSessionProvider implements VaadinSessionProvider {
    private static Logger log = LoggerFactory.getLogger(DefaultVaadinSessionProvider.class);

    @Override
    public VaadinSession get() {
        VaadinSession session = VaadinSession.getCurrent();

        // This may happen in background threads, or testing
        if (session == null) {
            String msg = "Vaaadin session not present.  If you are testing, use a Mock for this provider";
            log.warn(msg);
            throw new IllegalStateException(msg);
        }

        return session;
    }

}
