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
package uk.co.q3c.v7.demo.view.components;

import javax.inject.Inject;
import javax.inject.Named;

import uk.co.q3c.v7.util.A;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

// TODO i18N
public class InfoBar extends HorizontalLayout {

	private final Label titleLabel;
	private Label vaadinVersion;
	private ColorPanel colorPanel;

	@Inject
	protected InfoBar(@Named(A.title) String title, @Named(A.version) String version) {
		super();
		titleLabel = new Label();
		setSpacing(true);
		constructAdditionalComponents();
		configureComponents(title, version);
		addComponent(titleLabel);
		addComponent(colorPanel);
	}

	private void configureComponents(String title, String version) {
		titleLabel.setValue(title);
		vaadinVersion.setValue(version);

		// TODO Theme style
		// titleLabel.addStyleName("h2");
	}

	private void constructAdditionalComponents() {
		colorPanel = new ColorPanel();

		vaadinVersion = new Label();
		vaadinVersion.setImmediate(false);
		vaadinVersion.setWidth("-1px");
		vaadinVersion.setHeight("-1px");
		vaadinVersion.setValue("version");
		colorPanel.addComponent(vaadinVersion);
	}

}
