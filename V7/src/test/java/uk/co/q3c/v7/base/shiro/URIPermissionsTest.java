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
package uk.co.q3c.v7.base.shiro;

import static org.assertj.core.api.Assertions.*;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Test;

/**
 * The implies() parameter is the one being tested for, and would typically be uses in the context of hasPermission().
 * Thus "uri.*".implies("uri:view") will return true. The reverse will not
 * 
 * @author David Sowerby 19 Jul 2013
 * 
 */
public class URIPermissionsTest {

	WildcardPermission wp1;
	WildcardPermission wp2;
	WildcardPermission wp3;

	/**
	 * Not really a test, just Shiro functionality confirmation
	 */
	@Test
	public void checkFunctionality() {

		// given
		String s1 = "uri:view:*";
		String s2 = "uri:view:private:deptx:teamy";

		// when
		wp1 = new WildcardPermission(s1);
		wp2 = new WildcardPermission(s2);
		// then
		assertThat(wp1.implies(wp2)).isTrue();

	}

	@Test
	public void uri() {

		// given
		String userPermission1 = "uri:view:private:deptx:*";
		String userPermission2 = "uri:view:private:depty:*";
		String page = "private/deptx/teamy";
		String pagePermission = "uri:view:" + page.replace("/", ":");
		// when
		wp1 = new WildcardPermission(userPermission1);
		wp2 = new WildcardPermission(userPermission2);
		wp3 = new WildcardPermission(pagePermission);
		// then
		assertThat(wp1.implies(wp3)).isTrue();
		assertThat(wp2.implies(wp3)).isFalse();

	}
}
