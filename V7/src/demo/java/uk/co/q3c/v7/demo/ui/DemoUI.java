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
package uk.co.q3c.v7.demo.ui;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.demo.usage.DemoUsage;
import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;
import uk.co.q3c.v7.demo.view.components.InfoBar;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;

@PreserveOnRefresh
public class DemoUI extends BasicUI {

	@Inject
	protected DemoUI(HeaderBar headerBar, FooterBar footerBar, InfoBar infoBar, V7Navigator navigator,
			ErrorHandler errorHandler, DemoUsage usageLog, ConverterFactory converterFactory) {
		super(headerBar, footerBar, infoBar, navigator, errorHandler, converterFactory);

	}

}
