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

import javax.security.auth.login.AccountLockedException;

import org.apache.shiro.authc.ConcurrentAccessException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

import uk.co.q3c.v7.base.view.LoginView;

/**
 * Implementations should handle all Shiro exceptions captured during the login process.
 * 
 * @author David Sowerby 14 Jan 2013
 * 
 */
public interface LoginExceptionHandler {
	/**
	 * Response to an {@link UnknownAccountException}. This would be the response to a "normal" login failure - that is,
	 * before it becomes an {@link #excessiveAttempts(LoginView, UsernamePasswordToken)} event
	 * 
	 * @param loginView
	 * @param token
	 */
	void unknownAccount(LoginView loginView, UsernamePasswordToken token);

	/**
	 * Response to {@link IncorrectCredentialsException}. See the javadoc of the exception
	 * 
	 * @param loginView
	 * @param token
	 */
	void incorrectCredentials(LoginView loginView, UsernamePasswordToken token);

	/**
	 * Response to {@link ExpiredCredentialsException}. See the javadoc of the exception
	 * 
	 * @param loginView
	 * @param token
	 */
	void expiredCredentials(LoginView loginView, UsernamePasswordToken token);

	/**
	 * Response to {@link AccountLockedException}. See the javadoc of the exception
	 * 
	 * @param loginView
	 * @param token
	 */
	void accountLocked(LoginView loginView, UsernamePasswordToken token);

	/**
	 * Response to an {@link ExcessiveAttemptsException}, which occurs when a system is configured to raise an exception
	 * when there is a specified limit to the number of times a user can try and login. A login failure beofer that
	 * threshold is reached is handled by {@link #unknownAccount(LoginView, UsernamePasswordToken)}
	 * 
	 * @param loginView
	 * @param token
	 */
	void excessiveAttempts(LoginView loginView, UsernamePasswordToken token);

	/**
	 * Response to {@link ConcurrentAccessException}. See the javadoc of the exception
	 * 
	 * @param loginView
	 * @param token
	 */
	void concurrentAccess(LoginView loginView, UsernamePasswordToken token);

	/**
	 * Response to {@link DisabledAcoountException}. See the javadoc of the exception
	 * 
	 * @param loginView
	 * @param token
	 */
	void disabledAccount(LoginView loginView, UsernamePasswordToken token);

}
