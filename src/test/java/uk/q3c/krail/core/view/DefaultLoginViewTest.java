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

package uk.q3c.krail.core.view;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Button;
import net.engio.mbassy.bus.MBassador;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.shiro.LoginExceptionHandler;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.status.UserStatusBusMessage;
import uk.q3c.krail.core.view.component.LoginFormException;
import uk.q3c.krail.i18n.Translate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultLoginViewTest {

    DefaultLoginView view;

    @Mock
    Translate translate;

    @Mock
    LoginExceptionHandler loginExceptionHandler;


    @Mock
    MBassador<BusMessage> eventBus;
    @Mock
    Subject subject;
    @Mock
    private Button.ClickEvent clickEvent;
    @Mock
    private SubjectProvider subjectProvider;

    @Before
    public void setup() {
        when(subjectProvider.get()).thenReturn(subject);
        view = new DefaultLoginView(loginExceptionHandler, subjectProvider, translate, eventBus);
    }

    @Test
    public void buildView() {
        //given
        BeforeViewChangeBusMessage event = new BeforeViewChangeBusMessage(new NavigationState(), new NavigationState());
        //when

        view.buildView(event);
        //then
        assertThat(view.getRootComponent()).isNotNull();
    }

    @Test
    public void submitButton_clicked() {
        //given
        BeforeViewChangeBusMessage event = new BeforeViewChangeBusMessage(new NavigationState(), new NavigationState());
        view.buildView(event);
        //when
        view.getUsernameBox()
            .setValue("ds");
        view.getPasswordBox()
            .setValue("password");
        view.buttonClick(clickEvent);
        //then
        verify(eventBus).publish(any(UserStatusBusMessage.class));
        // TODO test for specific message
    }

    @Test(expected = LoginFormException.class)
    public void username_empty() {
        //given
        BeforeViewChangeBusMessage event = new BeforeViewChangeBusMessage(new NavigationState(), new NavigationState());
        view.buildView(event);
        //when
        view.getPasswordBox()
            .setValue("password");
        view.buttonClick(clickEvent);
        //then
    }

    @Test(expected = LoginFormException.class)
    public void password_empty() {
        //given
        BeforeViewChangeBusMessage event = new BeforeViewChangeBusMessage(new NavigationState(), new NavigationState());
        view.buildView(event);
        //when
        view.getUsernameBox()
            .setValue("ds");
        view.buttonClick(clickEvent);
        //then
    }
}