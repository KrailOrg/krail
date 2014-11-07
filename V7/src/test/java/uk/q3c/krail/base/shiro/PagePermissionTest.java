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
package uk.q3c.krail.base.shiro;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.base.navigate.NavigationState;
import uk.q3c.krail.base.navigate.StrictURIFragmentHandler;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class PagePermissionTest {

    @Inject
    StrictURIFragmentHandler uriHandler;

    @Test
    public void create() {

        // given

        String uri = "private/wiggly/id=1";
        NavigationState navigationState = uriHandler.navigationState(uri);

        // when
        PagePermission p = new PagePermission(navigationState);
        // then
        // for some reason parts are stored by WildcardPermission with [] around them
        assertThat(p.toString()).isEqualTo("[page]:[view]:[private]:[wiggly]");
        assertThat(p).isEqualTo(new WildcardPermission("page:view:private:wiggly"));

    }

    @Test
    public void createWithWildcard() {

        // given
        String uri = "private/wiggly/id=1";
        NavigationState navigationState = uriHandler.navigationState(uri);

        // when
        PagePermission p = new PagePermission(navigationState, true);
        // then
        // for some reason parts are stored by WildcardPermission with [] around them
        assertThat(p).isEqualTo(new WildcardPermission("page:view:private:wiggly:*"));

    }

    @Test
    public void createWithEditAndWildcard() {

        // given
        String uri = "private/wiggly/id=1";
        NavigationState navigationState = uriHandler.navigationState(uri);
        // when
        PagePermission p = new PagePermission(navigationState, true, true);
        // then
        assertThat(p).isEqualTo(new WildcardPermission("page:edit:private:wiggly:*"));
    }

    @Test
    public void createWithEdit() {

        // given
        String uri = "private/wiggly/id=1";
        NavigationState navigationState = uriHandler.navigationState(uri);
        // when
        PagePermission p = new PagePermission(navigationState, false, true);
        // then
        assertThat(p).isEqualTo(new WildcardPermission("page:edit:private:wiggly"));
    }

    @Test
    public void implies() {

        // given
        String uri = "private/wiggly/id=1";
        NavigationState navigationState = uriHandler.navigationState(uri);
        WildcardPermission wcp = new WildcardPermission("page:view:private:*");
        // when
        PagePermission p = new PagePermission(navigationState, true);
        // then
        assertThat(p.implies(wcp)).isFalse();
        assertThat(wcp.implies(p)).isTrue();
    }

}
