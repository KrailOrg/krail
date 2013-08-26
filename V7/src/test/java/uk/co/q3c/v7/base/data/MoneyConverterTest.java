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

import static org.fest.assertions.Assertions.*;

import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyFormatException;
import org.junit.Test;

public class MoneyConverterTest {
	MoneyConverter converter;

	@Test
	public void types() {

		// given
		converter = new MoneyConverter();
		// when

		// then
		assertThat(converter.getModelType()).isEqualTo(Money.class);
		assertThat(converter.getPresentationType()).isEqualTo(String.class);

	}

	@Test
	public void convertToModelNoSeparator() {

		// given
		converter = new MoneyConverter();
		// when
		Money money = converter.convertToModel("1025.44", Money.class, Locale.UK);
		// then
		Money expected = Money.ofMinor(CurrencyUnit.getInstance(Locale.UK), 102544);
		assertThat(money).isEqualTo(expected);
	}

	@Test
	public void convertToModelWithSeparator() {

		// given
		converter = new MoneyConverter();
		// when
		Money money = converter.convertToModel("1,025.44", Money.class, Locale.UK);
		// then
		Money expected = Money.ofMinor(CurrencyUnit.getInstance(Locale.UK), 102544);
		assertThat(money).isEqualTo(expected);
	}

	@Test
	public void convertToModelWithSeparatorAndSymbol() {

		// given
		converter = new MoneyConverter();
		// when
		Money money = converter.convertToModel("1,025.44", Money.class, Locale.UK);
		// then
		Money expected = Money.ofMinor(CurrencyUnit.getInstance(Locale.UK), 102544);
		assertThat(money).isEqualTo(expected);
	}

	@Test(expected = MoneyFormatException.class)
	public void convertToModeFormatException() {

		// given
		converter = new MoneyConverter();
		// when
		Money money = converter.convertToModel("$1,025.44", Money.class, Locale.UK);
		// then
		Money expected = Money.ofMinor(CurrencyUnit.getInstance(Locale.UK), 102544);
		assertThat(money).isEqualTo(expected);
	}

}
