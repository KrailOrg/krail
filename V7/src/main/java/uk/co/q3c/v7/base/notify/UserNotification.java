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
package uk.co.q3c.v7.base.notify;

/**
 * Implementations of this provide a means of notifying a user with a message, and are used in conjunction with
 * {@link UserNotifier}. There are a set of sub-interfaces to identify the message as either an Error, Warning or
 * Information message
 * <p>
 * The intended use of this, and its sub-interfaces, is via the {@link UserNotifier}, which enables I18N support and
 * provides a configurable way of selecting notification methods.
 * 
 * @author David Sowerby
 * 
 */
public interface UserNotification {

	public void message(String message);

}
