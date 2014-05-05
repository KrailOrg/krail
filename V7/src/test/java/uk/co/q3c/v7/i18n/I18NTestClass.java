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

public class I18NTestClass {

	@I18N(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
	private final Button buttonWithAnnotation;

	@I18N(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
	private final Label label;

	@I18N(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
	private final Table table;

	@TestI18N(caption = TestLabelKey.Yes, description = TestLabelKey.Yes, value = TestLabelKey.No)
	private final Label demoLabel;

	@I18N(caption = LabelKey.Field)
	private final TestCompositeComponent ccs;

	// Class annotation
	private final TestCompositeComponent ccc;

	@I18N
	private final TestCompositeNonComponent cnc;

	@I18N(caption = LabelKey.Field)
	private final TestCompositeComponentNested ccn;

	@I18N(caption = LabelKey.Yes, locale = "de-DE")
	private final Button specificLocale;

	@I18NFlex(captionKeyClass = TestLabelKey.class, captionKeyName = "Transfers", descriptionKeyClass = TestLabelKey.class, descriptionKeyName = "Home")
	private final Button flex;

	@I18NValue(value = LabelKey.Guest)
	private final Label value;

	@I18NValueFlex(valueKeyClass = TestLabelKey.class, valueKeyName = "Private")
	private final Label flexValue;

	@I18NValue(value = LabelKey.Yes, locale = "de-DE")
	private final Label valueLocale;

	@I18NValueFlex(valueKeyClass = TestLabelKey.class, valueKeyName = "Yes", locale = "de-DE")
	private final Label flexValueLocale;

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

		cnc = new TestCompositeNonComponent();
		ccn = new TestCompositeComponentNested();
		ccs = new TestCompositeComponent();
		ccc = new TestCompositeComponent();
		specificLocale = new Button();
		flex = new Button();
		value = new Label();
		flexValue = new Label();
		valueLocale = new Label();
		flexValueLocale = new Label();
	}

	private void setupTableColumns() {
		table.addContainerProperty(LabelKey.Small, String.class, "numpty");
		table.addContainerProperty(LabelKey.Cancel, String.class, "numpty");
		table.addContainerProperty("not i18N", String.class, "numpty");
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

	public TestCompositeNonComponent getCnc() {
		return cnc;
	}

	public TestCompositeComponentNested getCcn() {
		return ccn;
	}

	public Label getDemoLabel() {
		return demoLabel;
	}

	public TestCompositeComponent getCcs() {
		return ccs;
	}

	public Button getSpecificLocale() {
		return specificLocale;
	}

	public TestCompositeComponent getCcc() {
		return ccc;
	}

	public Button getFlex() {
		return flex;
	}

	public Label getValue() {
		return value;
	}

	public Label getFlexValue() {
		return flexValue;
	}

	public Label getValueLocale() {
		return valueLocale;
	}

	public Label getFlexValueLocale() {
		return flexValueLocale;
	}

}
