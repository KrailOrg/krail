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

package uk.q3c.krail.core.shiro;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class SubjectProviderTest extends AbstractShiroTest {

    @Mock
    VaadinService service;

    @Mock
    VaadinSession session;

    @Mock
    VaadinSessionProvider vsp;

    SubjectProvider provider;


    @BeforeClass
    public static void beforeClass() {
        setSecurityManager(new KrailSecurityManager());
    }

    @Before
    public void setup() {
        when(vsp.get()).thenReturn(session);
        provider = new SubjectProvider(vsp);
    }


    @Test
    public void get() {
        //given
        when(session.hasLock()).thenReturn(true);
        when(session.getAttribute(Subject.class)).thenReturn(null);
        //when
        Subject actual = provider.get();
        //then
        assertThat(actual).isNotNull();
        verify(session).setAttribute(eq(Subject.class), any(Subject.class));
        //given
        when(session.getAttribute(Subject.class)).thenReturn(actual);

        //when
        Subject actual2 = provider.get();

        //then
        assertThat(actual2).isEqualTo(actual);
    }
}