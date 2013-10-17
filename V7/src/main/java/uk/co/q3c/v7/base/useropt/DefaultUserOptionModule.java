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
package uk.co.q3c.v7.base.useropt;

import com.google.inject.AbstractModule;

public class DefaultUserOptionModule extends AbstractModule {

	public DefaultUserOptionModule() {
	}

	@Override
	protected void configure() {
		bindUserOption();
		bindUserOptionStore();
	}

	/**
	 * Override this method to provide your own {@link UserOption} implementation. If all you want to do is change the
	 * storage method, override {@link #bindUserOptionStore()} instead
	 */
	protected void bindUserOption() {
		bind(UserOption.class).to(DefaultUserOption.class);
	}

	/**
	 * Override this method to provide your own store implementation for user options. This is in effect a DAO
	 * implementation
	 */
	protected void bindUserOptionStore() {
		bind(UserOptionStore.class).to(DefaultUserOptionStore.class);

	}

}
