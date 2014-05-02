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

	@I18N(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok, value = LabelKey.Ok)
	private final Label label;

	@I18N(caption = LabelKey.Ok, description = DescriptionKey.Confirm_Ok)
	private final Table table;

	@TestI18N(caption = TestLabelKey.Yes, description = TestLabelKey.Yes, value = TestLabelKey.No)
	private final Label demoLabel;

	@I18N(caption = LabelKey.Field)
	private final TestCompositeComponent ccs;

	private final TestCompositeComponent_componentNotConstructed ccs_unconstructed;

	@I18N
	private final TestCompositeNonComponent cnc;

	@I18N(caption = LabelKey.Field)
	private final TestCompositeComponentNested ccn;

	@I18N(caption = LabelKey.Yes, locale = "de-DE")
	private final Button specificLocale;

	private final Button buttonWithoutAnnotation;
	private final Integer integer;
	private double dubble;
	// keep this as null make sure nothing breaks;
	@I18N(caption = LabelKey.Cancel)
	private Button unconstructed;

	protected I18NTestClass() {
		super();
		buttonWithAnnotation = new Button();
		buttonWithoutAnnotation = new Button();
		label = new Label();
		demoLabel = new Label();
		integer = new Integer(5);
		table = new Table();
		setupTableColumns();

		ccs_unconstructed = new TestCompositeComponent_componentNotConstructed();
		cnc = new TestCompositeNonComponent();
		ccn = new TestCompositeComponentNested();
		ccs = new TestCompositeComponent();
		specificLocale = new Button();
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

	public TestCompositeComponent_componentNotConstructed getCcs_unconstructed() {
		return ccs_unconstructed;
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

}
