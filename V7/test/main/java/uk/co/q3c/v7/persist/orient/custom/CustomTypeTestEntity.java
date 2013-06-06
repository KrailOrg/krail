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
package uk.co.q3c.v7.persist.orient.custom;

import java.util.Locale;

import org.joda.time.DateTime;

import uk.co.q3c.v7.demo.data.EntityBase;

/**
 * A test class only - used to hold each of the custom types used in OrientDb, to enable testing of the serialisation /
 * de-serialisation methods.
 * 
 * @author David Sowerby 29 Jan 2013
 * 
 */
public class CustomTypeTestEntity extends EntityBase {

	private DateTime dateTime;
	private Locale locale;

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
