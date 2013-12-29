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

import static org.fest.assertions.Assertions.*;

import com.google.inject.Inject;

import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultLoginAttemptLogTest {

	@Inject
	DefaultLoginAttemptLog attemptLog;

	String username = "anyone";

	@Test
	public void recordUnsuccessful() {

		// given
		attemptLog.setMaximumAttempts(3);
		UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
		// when
		attemptLog.recordFailedAttempt(token);
		// then
		assertThat(attemptLog.failedAttempts(username)).isEqualTo(1);
		// when
		attemptLog.recordFailedAttempt(token);
		// then
		assertThat(attemptLog.failedAttempts(username)).isEqualTo(2);

	}

	@Test
	public void clearUnsuccessful() {

		// given
		attemptLog.setMaximumAttempts(3);
		UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
		attemptLog.recordFailedAttempt(token);
		attemptLog.recordFailedAttempt(token);
		// when
		attemptLog.clearUnsuccessful(username);
		// then
		assertThat(attemptLog.failedAttempts(username)).isEqualTo(0);
		// when clear non-existent user
		attemptLog.clearUnsuccessful("wiggly");
		// then
		assertThat(attemptLog.failedAttempts("wiggly")).isEqualTo(0);

	}

	@Test(expected = ExcessiveAttemptsException.class)
	public void recordUnsuccessfulMaxExceeded() {

		// given
		attemptLog.setMaximumAttempts(3);
		UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
		// when
		attemptLog.recordFailedAttempt(token);
		attemptLog.recordFailedAttempt(token);
		attemptLog.recordFailedAttempt(token);
		attemptLog.recordFailedAttempt(token);
		// then
		// exception expected
	}

	@Test
	public void recordSuccessful() {

		// given
		attemptLog.setMaximumAttempts(3);
		UsernamePasswordToken token = new UsernamePasswordToken(username, "password");
		// when
		attemptLog.recordSuccessfulAttempt(token);
		// then
		assertThat(attemptLog.dateOfLastSuccess(username)).isNotNull();
		DateTime lastLogin = attemptLog.dateOfLastSuccess(username);
		assertThat(attemptLog.successfulAttempts(username)).isEqualTo(1);
		assertThat(DateTime.now().getMillis() - lastLogin.getMillis()).isLessThan(100);

	}

}
