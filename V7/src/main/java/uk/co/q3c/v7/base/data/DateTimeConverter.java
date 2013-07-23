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
package uk.co.q3c.v7.base.data;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;

import com.vaadin.data.util.converter.Converter;

/**
 * Implements a Converter to handle the Joda DateTime type
 * 
 * @author David Sowerby 1 Apr 2013
 * 
 */
public class DateTimeConverter implements Converter<Date, DateTime> {

	@Override
	public DateTime convertToModel(Date value,
			Class<? extends DateTime> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		return new DateTime(value);

	}

	@Override
	public Date convertToPresentation(DateTime value,
			Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.toDate();
	}

	@Override
	public Class<DateTime> getModelType() {
		return DateTime.class;
	}

	@Override
	public Class<Date> getPresentationType() {
		return Date.class;
	}
}
