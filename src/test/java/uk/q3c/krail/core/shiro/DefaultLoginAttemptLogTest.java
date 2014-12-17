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

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.shiro.DefaultLoginAttemptLog.LogEntry;
import uk.q3c.krail.core.shiro.DefaultLoginAttemptLog.LogOutcome;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultLoginAttemptLogTest {

    @Inject
    DefaultLoginAttemptLog attemptLog;

    String username = "anyone";
    String username1 = "doctor who";

    @Test
    public void recordUnsuccessful() {

        // given
        attemptLog.setMaximumAttempts(3);
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
        // when
        attemptLog.recordFailedAttempt(token);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(2);
        // when
        attemptLog.recordFailedAttempt(token);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(1);
        // when
        attemptLog.recordSuccessfulAttempt(token);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(3);

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
        LocalDateTime lastLogin = attemptLog.dateOfLastSuccess(username);
        assertThat(Duration.between(lastLogin, LocalDateTime.now())
                           .getSeconds()).isLessThan(2);
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(3);
    }

    @Test
    public void history() {

        // given
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
        UsernamePasswordToken token1 = new UsernamePasswordToken(username1, "anything");

        // when
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordSuccessfulAttempt(token);
        // then

        assertThat(attemptLog.historyFor(username)).hasSize(3);
        assertThat(attemptLog.historyFor(username1)).hasSize(2);
    }

    @Test
    public void clearHistoryForUser() {

        // given
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
        UsernamePasswordToken token1 = new UsernamePasswordToken(username1, "anything");
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordSuccessfulAttempt(token);

        // when
        attemptLog.clearHistory(username1);
        // then

        assertThat(attemptLog.historyFor(username)).hasSize(3);
        assertThat(attemptLog.historyFor(username1)).hasSize(0);
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(3);
        assertThat(attemptLog.attemptsRemaining(username1)).isEqualTo(1);

    }

    @Test
    public void clearAllHistory() {

        // given
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
        UsernamePasswordToken token1 = new UsernamePasswordToken(username1, "anything");
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordSuccessfulAttempt(token);

        // when
        attemptLog.clearHistory();
        // then

        assertThat(attemptLog.historyFor(username)).hasSize(0);
        assertThat(attemptLog.historyFor(username1)).hasSize(0);
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(3);
        assertThat(attemptLog.attemptsRemaining(username1)).isEqualTo(1);
    }

    @Test
    public void lastLogEntry() {

        // given
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
        UsernamePasswordToken token1 = new UsernamePasswordToken(username1, "anything");
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordSuccessfulAttempt(token);
        // when
        LogEntry log = attemptLog.latestLog(username);
        LogEntry log1 = attemptLog.latestLog(username1);
        // then
        assertThat(log.getLogOutcome()).isEqualTo(LogOutcome.PASS);
        assertThat(log1.getLogOutcome()).isEqualTo(LogOutcome.FAIL);
        // when
        attemptLog.resetAttemptCount(username);
        log = attemptLog.latestLog(username);
        // then
        assertThat(log.getLogOutcome()).isEqualTo(LogOutcome.RESET);
    }

    @Test
    public void changeMaxAttempts() {

        // given
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");

        // when
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(1);
        // when
        attemptLog.setMaximumAttempts(10);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(8);
    }

    @Test
    public void resetAttemptCountForUser() {

        // given
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");

        // when
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(1);
        // when
        attemptLog.resetAttemptCount(username);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(3);
    }

    @Test
    public void resetAttemptCountForAll() {

        // given
        UsernamePasswordToken token = new UsernamePasswordToken(username, "anything");
        UsernamePasswordToken token1 = new UsernamePasswordToken(username1, "anything");

        // when
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token);
        attemptLog.recordFailedAttempt(token1);
        attemptLog.recordFailedAttempt(token1);
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(1);
        assertThat(attemptLog.attemptsRemaining(username1)).isEqualTo(1);
        // when
        attemptLog.resetAttemptCount();
        // then
        assertThat(attemptLog.attemptsRemaining(username)).isEqualTo(3);
        assertThat(attemptLog.attemptsRemaining(username1)).isEqualTo(3);
    }

}
