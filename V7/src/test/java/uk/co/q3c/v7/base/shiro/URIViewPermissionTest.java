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

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.URIFragmentHandler;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class URIViewPermissionTest {

	@Mock
	URIFragmentHandler uriHandler;

	@Test
	public void create() {

		// given
		String uri = "private/wiggly/id=1";
		when(uriHandler.virtualPage()).thenReturn("private/wiggly");

		// when
		URIViewPermission p = new URIViewPermission(uriHandler, uri);
		// then
		// for some reason parts are stored by WildcardPermission with [] around them
		assertThat(p.toString()).isEqualTo("[uri]:[view]:[private]:[wiggly]");
		assertThat(p).isEqualTo(new WildcardPermission("uri:view:private:wiggly"));

	}

	@Test
	public void createWithWildcard() {

		// given
		String uri = "private/wiggly/id=1";
		when(uriHandler.virtualPage()).thenReturn("private/wiggly");

		// when
		URIViewPermission p = new URIViewPermission(uriHandler, uri, true);
		// then
		// for some reason parts are stored by WildcardPermission with [] around them
		assertThat(p).isEqualTo(new WildcardPermission("uri:view:private:wiggly:*"));

	}

}
