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
 * The implementation of this interface handles login and logout notifications and acts as a reference point for user
 * interface components and other {@link UI} related objects to monitor changes in the {@link Subject}'s authentication
 * status. Components which want to be notified of user login status changes (login or logout) should register
 * themselves as listeners.
 * 
 * @author David Sowerby 16 Sep 2013
 * 
 */
public interface LoginStatusHandler {

	/**
	 * Called by the component initiating the login / logout. This component -typically, but not necessarily, a
	 * {@link LoginView} or {@link LogoutView} will be resident in a {@link ScopedUI}, and the instance of this
	 * implementation (which must be UIScoped) then notifies other {@link ScopedUI} instances of the change.
	 */
	void initiateStatusChange();

	/**
	 * Called to respond to the change, where {@code authenticated} if the current Subject is authenticated, and
	 * {@code name} is the user's name. If the user is not authenticated, {@code name} will be 'guest'
	 * 
	 * @param name
	 * @param status
	 */
	void respondToStatusChange(boolean authenticated, String name);

	/**
	 * When a UI is first created, components may need to access the status to determine what they display. Although
	 * that could be directly accessed from {@link SubjectProvider}, the listener will already have an instance of this
	 * implementation.
	 */
	boolean subjectIsAuthenticated();

	/**
	 * When a UI is first created, components may need to the Subject's name. Although that could be directly accessed
	 * from {@link SubjectProvider}, the listener will already have an instance of this implementation. Using this
	 * method will also ensure a standard interpretation of 'guest'
	 */
	String subjectName();

	public abstract void removeListener(LoginStatusListener listener);

	public abstract void addListener(LoginStatusListener listener);

}
