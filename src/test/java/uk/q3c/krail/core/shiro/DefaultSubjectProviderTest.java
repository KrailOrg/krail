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
import com.vaadin.server.VaadinSession;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.util.ThreadContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultSubjectProviderTest extends AbstractShiroTest {


    SubjectProvider provider;


    @Mock
    KrailSecurityManager krailSecurityManager;

    @Mock
    VaadinSession vaadinSession;

    @Mock
    Subject mockSubject;

    @Mock
    Subject mockSubject2;

    @Mock
    private PrincipalCollection principals;

    @Mock
    Lock sessionLock;


    @Before
    public void setup() {
        VaadinSession.setCurrent(vaadinSession);
        setSecurityManager(krailSecurityManager);
        ThreadContext.unbindSubject();
        provider = new DefaultSubjectProvider(krailSecurityManager);
        when(mockSubject.getPrincipals()).thenReturn(principals);
        when(krailSecurityManager.createSubject(any(SubjectContext.class))).thenReturn(mockSubject2);
        when(vaadinSession.getLockInstance()).thenReturn(sessionLock);
    }


    @Test
    public void get_no_previous() {
        //given
        final ArgumentCaptor<SubjectContext> captor = ArgumentCaptor.forClass(SubjectContext.class);
        //when
        Subject actual = provider.get();
        //then
        verify(vaadinSession).getAttribute(SubjectProvider.SUBJECT_ATTRIBUTE);
        verify(krailSecurityManager).createSubject(captor.capture());
        verify(vaadinSession).setAttribute(SubjectProvider.SUBJECT_ATTRIBUTE, actual.getPrincipals());


        final SubjectContext argument = captor.getValue();
        assertThat(argument.getPrincipals()).isNull();

    }


    @Test
    public void get_with_previous() {
        //given
        final ArgumentCaptor<SubjectContext> captor = ArgumentCaptor.forClass(SubjectContext.class);
        when(vaadinSession.getAttribute(SubjectProvider.SUBJECT_ATTRIBUTE)).thenReturn(principals);
        //when
        Subject actual = provider.get();
        //then
        assertThat(actual).isEqualTo(mockSubject2);
        verify(krailSecurityManager).createSubject(captor.capture());
        final SubjectContext argument = captor.getValue();
        assertThat(argument.getPrincipals()).isEqualTo(principals);

    }
}