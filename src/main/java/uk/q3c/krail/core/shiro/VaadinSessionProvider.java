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
package uk.q3c.krail.core.shiro;

import com.vaadin.server.VaadinSession;
import org.apache.shiro.subject.Subject;

/**
 * The provides an interface for an implementation to wrap the static method of obtaining the current VaadinSession,
 * mainly to enable mocking for tests. The current {@link Subject} is stored in the VaadinSession, so if a Subject is
 * used in a background thread, some other means of storing it would be required
 *
 * @author David Sowerby 15 Sep 2013
 */
public interface VaadinSessionProvider {

    public abstract VaadinSession get();

}
