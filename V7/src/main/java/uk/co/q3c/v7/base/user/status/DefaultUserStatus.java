/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.base.user.status;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.SubjectProvider;

import java.util.LinkedList;
import java.util.List;

@VaadinSessionScoped
public class DefaultUserStatus implements UserStatus {

    private static Logger log = LoggerFactory.getLogger(DefaultUserStatus.class);
    private final List<UserStatusListener> listeners;
    private final V7Navigator navigator;
    private SubjectProvider subjectProvider;

    @Inject
    protected DefaultUserStatus(V7Navigator navigator, SubjectProvider subjectProvider) {
        super();
        this.navigator = navigator;
        this.subjectProvider = subjectProvider;
        listeners = new LinkedList<>();
    }

    @Override
    public void addListener(UserStatusListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(UserStatusListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void statusChanged() {
        fireListeners();
        log.debug("user status change listeners have been fired, now invoke the navigator");
        navigator.userStatusChanged();
    }

    protected void fireListeners() {
        log.debug("firing user status change listeners");
        for (UserStatusListener listener : listeners) {
            listener.userStatusChanged();
        }
    }

    @Override
    public boolean isAuthenticated() {
        return subjectProvider.get()
                              .isAuthenticated();
    }
}
