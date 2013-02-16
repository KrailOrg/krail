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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class I18NTestClass implements I18NListener {

	@I18N(caption = LabelKeys.ok, description = DescriptionKeys.ok)
	private final Button buttonWithAnnotation;

	@I18N(caption = LabelKeys.ok, description = DescriptionKeys.ok, value = DescriptionKeys.ok)
	private final Label label;

	@I18N(caption = LabelKeys.ok, description = DescriptionKeys.ok)
	private final Table table;

	private final Button buttonWithoutAnnotation;
	private final Integer integer;
	private double dubble;

	protected I18NTestClass() {
		super();
		buttonWithAnnotation = new Button();
		buttonWithoutAnnotation = new Button();
		label = new Label();
		integer = new Integer(5);
		table = new Table();
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

	public Label getLabel() {
		return label;
	}

	public Table getTable() {
		return table;
	}

}
