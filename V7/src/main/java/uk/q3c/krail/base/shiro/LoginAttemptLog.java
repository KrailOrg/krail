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
package uk.q3c.krail.base.shiro;

import com.google.common.collect.ImmutableList;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.joda.time.DateTime;
import uk.q3c.krail.base.shiro.DefaultLoginAttemptLog.LogEntry;

/**
 * An interface representing attempts by a Subject to login. Implementations should record attempts as required, and
 * also take the appropriate action when the maximum limit is exceeded
 *
 * @author David Sowerby 13 Jan 2013
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

    /**
     * Returns the date this user last logged in, or null if no successful login has been recorded
     *
     * @param username
     *
     * @return
     */
    DateTime dateOfLastSuccess(String username);

    /**
     * Clears the history of login attempts, but does NOT reset the number of attempts remaining (use
     * {@link #resetAttemptCount()} for that)
     *
     * @param username
     */
    void clearHistory(String username);

    /**
     * clear the history of login attempts for all users, but does NOT reset the number of attempts remaining (use
     * {@link #resetAttemptCount()} for that)
     */
    void clearHistory();

    /**
     * resets the remaining login attempts for the specified user, to the level set by {@link #setMaximumAttempts(int)}
     */
    void resetAttemptCount(String username);

    /**
     * resets the remaining login attempts for all users, to the level set by {@link #setMaximumAttempts(int)}
     */
    void resetAttemptCount();

    /**
     * Returns the number of login attempts this user still has left
     *
     * @param username
     *
     * @return
     */
    int attemptsRemaining(String username);

    /**
     * Returns the latest log entry for {@code username}
     *
     * @param username
     *
     * @return
     */
    LogEntry latestLog(String username);

    /**
     * returns a list of log entries for {@code username}, or an empty list if history exists
     *
     * @param username
     *
     * @return
     */

    ImmutableList<LogEntry> historyFor(String username);

}
