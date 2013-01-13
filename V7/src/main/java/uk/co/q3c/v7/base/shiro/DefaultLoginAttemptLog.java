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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Singleton;

import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.joda.time.DateTime;

@Singleton
public class DefaultLoginAttemptLog implements LoginAttemptLog {

	private int maxAttempts = 3;
	private final Map<String, Integer> unsuccessful = new TreeMap<>();
	private final Map<String, List<DateTime>> successful = new TreeMap<>();
	private final Map<String, DateTime> latestSuccessful = new TreeMap<>();

	@Override
	public void setMaximumAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	@Override
	public void recordSuccessfulAttempt(UsernamePasswordToken upToken) {
		String username = upToken.getUsername();
		List<DateTime> list = successful.get(username);
		if (list == null) {
			list = new ArrayList<>();
			successful.put(username, list);

		}
		DateTime now = DateTime.now();
		latestSuccessful.put(username, now);
		list.add(now);
	}

	@Override
	public void recordFailedAttempt(UsernamePasswordToken upToken) {
		Integer record = unsuccessful.get(upToken.getUsername());
		int count = 0;
		if (record == null) {
			count = 0;
		} else {
			count = record;
		}
		count++;
		if (count >= maxAttempts) {
			throw new ExcessiveAttemptsException("Login failed after maximum attempts");
		}
		unsuccessful.put(upToken.getUsername(), new Integer(count));
	}

	@Override
	public int failedAttempts(String username) {
		Integer record = unsuccessful.get(username);
		if (record == null) {
			return 0;
		} else {
			return record;
		}
	}

	@Override
	public DateTime dateOfLastSuccess(String username) {
		return latestSuccessful.get(username);
	}

	@Override
	public int successfulAttempts(String username) {
		return successful.get(username).size();
	}

	@Override
	public void clearUnsuccessful(String username) {
		unsuccessful.remove(username);
	}

}
