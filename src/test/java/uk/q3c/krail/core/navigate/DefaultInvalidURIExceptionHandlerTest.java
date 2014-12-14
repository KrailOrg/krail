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
package uk.q3c.krail.core.navigate;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.i18n.MessageKey;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultInvalidURIExceptionHandlerTest {

    @Mock
    Navigator navigator;

    @Mock
    UserNotifier notifier;

    DefaultInvalidURIExceptionHandler handler;

    NavigationState navState;

    @Before
    public void setup() {

        handler = new DefaultInvalidURIExceptionHandler(notifier);
        navState = new StrictURIFragmentHandler().navigationState("public/wiggly/id=3");
        when(navigator.getCurrentNavigationState()).thenReturn(navState);
    }

    @Test
    public void notify_() {

        // given
        InvalidURIException exception = new InvalidURIException();
        exception.setTargetURI(navState.getVirtualPage());
        // when
        handler.invoke(exception);
        // then
        verify(notifier).notifyInformation(MessageKey.Invalid_URI, navState.getVirtualPage());

    }
}
