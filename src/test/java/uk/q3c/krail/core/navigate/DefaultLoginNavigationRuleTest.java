/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.navigate;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.user.UserStatusChangeSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultLoginNavigationRuleTest {

    @Mock
    Navigator navigator;

    @Mock
    UserStatusChangeSource source;

    DefaultLoginNavigationRule rule;
    @Mock
    UserSitemapNode aPageNode;
    @Mock
    UserSitemapNode loginNode;
    @Mock
    NavigationState previousNavigationState;
    @Mock
    NavigationState homeNavigationState;
    @Mock
    private UserSitemapNode logoutNode;
    @Mock
    private UserSitemapNode previousNode;
    @Mock
    private URIFragmentHandler uriHandler;
    @Mock
    private UserSitemap userSitemap;

    @Before
    public void setup() {
        rule = new DefaultLoginNavigationRule(userSitemap, uriHandler);
        when(userSitemap.standardPageNode(StandardPageKey.Log_In)).thenReturn(loginNode);
        when(userSitemap.standardPageNode(StandardPageKey.Log_Out)).thenReturn(logoutNode);
    }

    @Test
    public void not_on_login_page() {
        //given
        when(navigator.getCurrentNode()).thenReturn(aPageNode);
        //when
        Optional<NavigationState> expected = rule.changedNavigationState(navigator, source);
        //then
        assertThat(expected.isPresent()).isFalse();
    }

    @Test
    public void on_login_page_no_previous() {
        //given
        when(navigator.getCurrentNode()).thenReturn(loginNode);
        when(navigator.getPreviousNode()).thenReturn(null);
        when(uriHandler.navigationState(any(String.class))).thenReturn(homeNavigationState);
        //when
        Optional<NavigationState> expected = rule.changedNavigationState(navigator, source);
        //then
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get()).isEqualTo(homeNavigationState);
    }

    @Test
    public void login_page_has_previous() {
        //given
        when(navigator.getCurrentNode()).thenReturn(loginNode);
        when(navigator.getPreviousNode()).thenReturn(previousNode);
        when(navigator.getPreviousNavigationState()).thenReturn(previousNavigationState);
        //when
        Optional<NavigationState> expected = rule.changedNavigationState(navigator, source);
        //then
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get()).isEqualTo(previousNavigationState);
    }

    @Test
    public void on_login_page_previous_was_logout() {
        //given
        when(navigator.getCurrentNode()).thenReturn(loginNode);
        when(navigator.getPreviousNode()).thenReturn(logoutNode);
        when(uriHandler.navigationState(any(String.class))).thenReturn(homeNavigationState);
        //when
        Optional<NavigationState> expected = rule.changedNavigationState(navigator, source);
        //then
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get()).isEqualTo(homeNavigationState);
    }
}