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

	@I18N(caption = LabelKeys.small, description = DescriptionKeys.small_font)
	private final Button buttonWithAnnotation;

	@I18N(caption = LabelKeys.small, description = DescriptionKeys.small_font, value = DescriptionKeys.small_font)
	private final Label label;

	@I18N(caption = LabelKeys.small, description = DescriptionKeys.small_font)
	private final Table table;

	@TestI18N(caption = TestLabelKeys.Yes, description = TestLabelKeys.Yes, value = TestLabelKeys.No)
	private final Label demoLabel;

	private final TestCompositeComponent ccs;

	private final TestCompositeNonComponent cnc;

	private final TestCompositeComponentNested ccsn;

	private final TestCompositeNonComponentNested cncn;

	private final Button buttonWithoutAnnotation;
	private final Integer integer;
	private double dubble;

	protected I18NTestClass() {
		super();
		buttonWithAnnotation = new Button();
		buttonWithoutAnnotation = new Button();
		label = new Label();
		demoLabel = new Label();
		integer = new Integer(5);
		table = new Table();
		setupTableColumns();

		ccs = new TestCompositeComponent();
		cnc = new TestCompositeNonComponent();
		ccsn = new TestCompositeComponentNested();
		cncn = new TestCompositeNonComponentNested();
	}

	private void setupTableColumns() {
		table.addContainerProperty(LabelKeys.small, String.class, "numpty");
		table.addContainerProperty(LabelKeys.cancel, String.class, "numpty");
		table.addContainerProperty("not i18N", String.class, "numpty");
	}

	@Override
	public void localeChange(I18NTranslator interpreter) {
		interpreter.translate(this);
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

	public TestCompositeComponent getCcs() {
		return ccs;
	}

	public TestCompositeNonComponent getCnc() {
		return cnc;
	}

	public TestCompositeComponentNested getCcsn() {
		return ccsn;
	}

	public TestCompositeNonComponentNested getCncn() {
		return cncn;
	}

	public Label getDemoLabel() {
		return demoLabel;
	}

}
