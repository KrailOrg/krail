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
package uk.co.q3c.v7.base.shiro;

import org.apache.shiro.subject.Subject;

import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;

import com.vaadin.ui.UI;

/**
 * The implementation of this interface handles login and logout notifications
 * and acts as a reference point for user interface components and other
 * {@link UI} related objects to monitor changes in the {@link Subject}'s
 * authentication status. Components which want to be notified of user login
 * status changes (login or logout) should register themselves as listeners.
 * 
 * @author David Sowerby 16 Sep 2013
 * 
 */
public interface LoginStatusHandler {

	/**
	 * Called by the V7SecurityManager after the login or logout.
	 */
	public void fireStatusChange(LoginStatusEvent event);

	public abstract void addListener(LoginStatusListener listener);

	public abstract void removeListener(LoginStatusListener listener);

}
