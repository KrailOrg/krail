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
package uk.co.q3c.v7.i18n;

import com.vaadin.ui.Button;

public class I18NTestClass implements I18NListener {

	@I18N(caption = LabelKeys.ok)
	private final Button buttonWithAnnotation;
	private final Button buttonWithoutAnnotation;
	private final Integer integer;
	private double dubble;

	protected I18NTestClass() {
		super();
		buttonWithAnnotation = new Button();
		buttonWithoutAnnotation = new Button();
		integer = new Integer(5);
	}

	@Override
	public void localeChange(I18NInterpreter interpreter) {
		interpreter.interpret(this);
	}

	public Button getButtonWithAnnotation() {
		return buttonWithAnnotation;
	}

	public Button getButtonWithoutAnnotation() {
		return buttonWithoutAnnotation;
	}

	public Integer getInteger() {
		return integer;
	}

	public double getDubble() {
		return dubble;
	}

}
