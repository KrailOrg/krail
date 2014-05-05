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

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 *
 * @author David Sowerby
 * @date 2 May 2014
 */
@I18N(caption = LabelKey.Class)
public class TestCompositeComponent extends Panel {

	@I18N(description = DescriptionKey.Confirm_Ok)
	private final Label label = new Label();

	public Label getLabel() {
		return label;
	}

}
