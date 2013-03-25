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
package uk.co.q3c.v7.base.ui.form;

import org.junit.Test;

import uk.co.q3c.v7.base.entity.TestEntity;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class FieldGroupTest {

	private TextField firstName;
	private TextField lastName;
	@PropertyId("address.streetAddress")
	private TextField streetAddress;

	@Test
	public void doit() {

		TestEntity te = new TestEntity();
		te.setFirstName("Mango");
		te.setLastName("Chutney");

		TestEntity te2 = new TestEntity();
		te2.setFirstName("Pickled");
		te2.setLastName("Eggs");

		FieldGroup fieldGroup = new BeanFieldGroup<TestEntity>(TestEntity.class);

		// these two calls can be either way round
		fieldGroup.setItemDataSource(new BeanItem<TestEntity>(te));
		fieldGroup.buildAndBindMemberFields(this);

		System.out.println(firstName.getCaption());
		System.out.println(firstName.getValidators().size());
		BeanValidator v = (BeanValidator) firstName.getValidators().toArray()[0];
		System.out.println(v.toString());
		Field<?> f = fieldGroup.getField("firstName");
		System.out.println(f.getValue());
		f = fieldGroup.getField("lastName");
		System.out.println(f.getValue());

		fieldGroup.setItemDataSource(new BeanItem<TestEntity>(te2));
		f = fieldGroup.getField("firstName");
		System.out.println(f.getValue());
	}

}
