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
package uk.co.q3c.util;

import com.vaadin.ui.Component;

/**
 * Utility class used to standardise id setting (setId methods in Components).
 * 
 * @author David Sowerby 15 Sep 2013
 * 
 */
public class ID {

	/**
	 * The qualifier is just a way of identifying one of several of the component in the same parent component. The
	 * components list amounts to the notional hierarchy of components (it can be anything that makes sense in your
	 * environment, this is just an identifier)
	 * 
	 * @param qualifier
	 * @param components
	 * @return
	 */
	public static String getId(String qualifier, Component... components) {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		for (Component c : components) {
			if (!first) {
				buf.append("-");
			} else {
				first = false;
			}
			buf.append(c.getClass().getSimpleName());
		}
		if (qualifier != null) {
			buf.append("-");
			buf.append(qualifier);
		}
		return buf.toString();
	}

	public static String getId(Component... components) {
		return getId(null, components);
	}

}
