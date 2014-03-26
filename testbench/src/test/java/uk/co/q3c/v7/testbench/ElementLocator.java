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
package uk.co.q3c.v7.testbench;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;

public class ElementLocator {

	private final ElementPath path;

	private final WebDriver driver;

	public ElementLocator(WebDriver driver, String context) {
		this.driver = driver;
		path = new ElementPath(context);
	}

	public TestBenchElementCommands get(ElementPath path) {
		WebElement webElement = driver.findElement(By.vaadin(path.get()));
		return (TestBenchElementCommands) webElement;
	}

	public WebElement get() {
		WebElement webElement = driver.findElement(By.vaadin(path.get()));
		return webElement;
	}

	public ElementLocator id(String id) {
		path.id(id);
		return this;
	}

	public ElementLocator index(int... i) {
		path.index(i);
		return this;
	}

	public ElementLocator expand() {
		path.expand();
		return this;
	}

	public ElementPath getPath() {
		return path;
	}

}
