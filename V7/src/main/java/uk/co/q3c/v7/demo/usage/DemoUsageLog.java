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
package uk.co.q3c.v7.demo.usage;

import org.joda.time.DateTime;

import uk.co.q3c.v7.base.entity.EntityBase;

public class DemoUsageLog extends EntityBase {

	private DateTime dateTime;
	private String event;
	private String sourceIP;
	private String localeString;

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getLocaleString() {
		return localeString;
	}

	public void setLocaleString(String localeString) {
		this.localeString = localeString;
	}

}
