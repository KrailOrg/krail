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

import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatException;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

import com.vaadin.data.util.converter.Converter;

/**
 * Implements a Converter to handle the Joda Money type
 * 
 * @author David Sowerby 1 Apr 2013
 * 
 */
public class MoneyConverter implements Converter<String, Money> {

	@Override
	public Money convertToModel(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		CurrencyUnit uk = CurrencyUnit.getInstance(Locale.UK);
		MoneyFormatter ukFormat = new MoneyFormatterBuilder().appendCurrencyCode()
				.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA).toFormatter();
		try {

			Money money = ukFormat.parseMoney(uk.getCurrencyCode() + value);
			return money;
		} catch (MoneyFormatException mfe) {
			if (value.contains(uk.getSymbol())) {
				String cleanedValue = value.replace(uk.getSymbol(), "");
				Money money = ukFormat.parseMoney(uk.getCurrencyCode() + cleanedValue);
				return money;
			} else {
				throw mfe;
			}
		}

	}

	@Override
	public String convertToPresentation(Money value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.toString();
	}

	@Override
	public Class<Money> getModelType() {
		return Money.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
