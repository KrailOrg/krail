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

import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.joda.time.DateTime;

/**
 * An interface representing attempts by a Subject to login. Implementations should record attempts as required, and
 * also take the appropriate action when the maximum limit is exceeded
 * 
 * @author David Sowerby 13 Jan 2013
 * 
 */
public interface LoginAttemptLog {

	void setMaximumAttempts(int maxAttempts);

	void recordSuccessfulAttempt(UsernamePasswordToken upToken);

	/**
	 * Implementations should record failed attempts and throw a {@link ExcessiveAttemptsException} if the maximum
	 * allowable attempts is exceeded
	 * 
	 * @param upToken
	 */
	void recordFailedAttempt(UsernamePasswordToken upToken);

	int failedAttempts(String username);

	DateTime dateOfLastSuccess(String username);

	int successfulAttempts(String username);

	void clearUnsuccessful(String username);

}
