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
import com.google.inject.Singleton;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Singleton
public class DefaultLoginAttemptLog implements LoginAttemptLog {

    private final Map<String, List<LogEntry>> history = new TreeMap<>();
    private final Map<String, Integer> unsuccessfulAttempts = new TreeMap<>();
    private final Map<String, DateTime> lastSuccessful = new TreeMap<>();
    private int maxAttempts = 3;

    @Override
    public void setMaximumAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public void recordSuccessfulAttempt(UsernamePasswordToken upToken) {
        LogEntry log = createLog(upToken, LogOutcome.PASS);
        unsuccessfulAttempts.remove(upToken.getUsername());
        lastSuccessful.put(upToken.getUsername(), log.dateTime);
    }

    private LogEntry createLog(UsernamePasswordToken upToken, LogOutcome logOutcome) {
        String username = upToken.getUsername();
        return createLog(username, logOutcome);
    }

    private LogEntry createLog(String username, LogOutcome logOutcome) {
        LogEntry logEntry = new LogEntry(logOutcome);
        List<LogEntry> list = history.get(username);
        if (list == null) {
            list = new ArrayList<>();
            history.put(username, list);

        }
        list.add(logEntry);
        return logEntry;
    }

    /**
     * records a failed login attempt and throws a ExcessiveAttemptsException if the number of attempts exceeds
     * {@link #maxAttempts}
     *
     * @see uk.q3c.krail.base.shiro.LoginAttemptLog#recordFailedAttempt(org.apache.shiro.authc.UsernamePasswordToken)
     */
    @Override
    public void recordFailedAttempt(UsernamePasswordToken upToken) {
        createLog(upToken, LogOutcome.FAIL);

        Integer failedAttempts = unsuccessfulAttempts.get(upToken.getUsername());
        if (failedAttempts == null) {
            failedAttempts = 0;
        }
        unsuccessfulAttempts.put(upToken.getUsername(), new Integer(failedAttempts + 1));
        int attemptsLeft = attemptsRemaining(upToken.getUsername());
        if (attemptsLeft == 0) {
            throw new ExcessiveAttemptsException("Login failed after maximum attempts");
        }
    }

    @Override
    public int attemptsRemaining(String username) {
        Integer attemptsMade = unsuccessfulAttempts.get(username);
        // no unsuccessful attempt has been made to login, there won't be an entry
        if (attemptsMade == null) {
            attemptsMade = 0;
        }
        return maxAttempts - attemptsMade;
    }

    @Override
    public void clearHistory(String username) {
        history.remove(username);
    }

    @Override
    public void resetAttemptCount(String username) {
        unsuccessfulAttempts.remove(username);
        createLog(username, LogOutcome.RESET);
    }

    @Override
    public void clearHistory() {
        history.clear();
    }

    @Override
    public void resetAttemptCount() {
        unsuccessfulAttempts.clear();
    }

    @Override
    public DateTime dateOfLastSuccess(String username) {
        return lastSuccessful.get(username);
    }

    @Override
    public LogEntry latestLog(String username) {
        List<LogEntry> list = history.get(username);
        return list.get(list.size() - 1);
    }

    @Override
    public ImmutableList<LogEntry> historyFor(String username) {
        List<LogEntry> list = history.get(username);
        if (list == null) {
            list = new ArrayList<>();
        }
        return ImmutableList.copyOf(list);
    }

    public enum LogOutcome {
        PASS, FAIL, RESET
    }

    public class LogEntry {
        private final DateTime dateTime;
        private final LogOutcome logOutcome;

        public LogEntry(LogOutcome logOutcome) {
            this.logOutcome = logOutcome;
            dateTime = DateTime.now();
        }

        public DateTime getDateTime() {
            return dateTime;
        }

        public LogOutcome getLogOutcome() {
            return logOutcome;
        }
    }

}
