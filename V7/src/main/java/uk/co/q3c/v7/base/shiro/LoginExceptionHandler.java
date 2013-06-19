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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ConcurrentAccessException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
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
	 * @param uae 
	 */
	void unknownAccount(LoginView loginView, UsernamePasswordToken token, UnknownAccountException uae);

	/**
	 * Response to {@link IncorrectCredentialsException}. See the javadoc of the exception
	 * 
	 * @param loginView
	 * @param token
	 * @param ice 
	 */
	void incorrectCredentials(LoginView loginView, UsernamePasswordToken token, IncorrectCredentialsException ice);

	/**
	 * Response to {@link ExpiredCredentialsException}. See the javadoc of the exception. Typically, the implementation
	 * of this method will navigate to a V7View which allows the user to update their password.
	 * 
	 * @param loginView
	 * @param token
	 * @param ece 
	 */
	void expiredCredentials(LoginView loginView, UsernamePasswordToken token, ExpiredCredentialsException ece);

	/**
	 * Response to {@link AccountLockedException}. See the javadoc of the exception. Typically, the implementation of
	 * this method will navigate to a V7View which allows the user to request that their account is unlocked, although
	 * it perhaps just inform the user and do nothing else.
	 * 
	 * @param loginView
	 * @param token
	 * @param lae 
	 */
	void accountLocked(LoginView loginView, UsernamePasswordToken token, LockedAccountException lae);

	/**
	 * Response to an {@link ExcessiveAttemptsException}, which occurs when a system is configured to raise an exception
	 * when there is a specified limit to the number of times a user can try and login. A login failure before that
	 * threshold is reached is handled by {@link #unknownAccount(LoginView, UsernamePasswordToken)}. Typically, the
	 * implementation of this method will navigate to a V7View which allows the user to request a reset after filling in
	 * appropriate security answers.
	 * 
	 * @param loginView
	 * @param token
	 * @param excess 
	 */
	void excessiveAttempts(LoginView loginView, UsernamePasswordToken token, ExcessiveAttemptsException excess);

	/**
	 * Response to {@link ConcurrentAccessException}. See the javadoc of the exception
	 * 
	 * @param loginView
	 * @param token
	 * @param cae 
	 */
	void concurrentAccess(LoginView loginView, UsernamePasswordToken token, ConcurrentAccessException cae);

	/**
	 * Response to {@link DisabledAcoountException}. See the javadoc of the exception. Typically, the implementation of
	 * this method will navigate to a V7View which allows the user to request that their account is re-enabled, although
	 * exact behaviour is up to the implementation.
	 * 
	 * @param loginView
	 * @param token
	 * @param dae 
	 */
	void disabledAccount(LoginView loginView, UsernamePasswordToken token, DisabledAccountException dae);

	/**
	 * Response to {@link AuthenticationException}. See the javadoc of the exception. Typically, the implementation of
	 * this method will let the user know that an unexpected error has happened, although
	 * exact behaviour is up to the implementation.
	 * 
	 * @param loginView
	 * @param token
	 * @param ae 
	 */
	void genericException(LoginView loginView, UsernamePasswordToken token, AuthenticationException ae);

}
