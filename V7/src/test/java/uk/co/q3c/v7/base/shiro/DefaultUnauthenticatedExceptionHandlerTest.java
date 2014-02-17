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

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.notify.UserNotifier;
import uk.co.q3c.v7.i18n.DescriptionKey;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Notification;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultUnauthenticatedExceptionHandlerTest {

	@Mock
	UserNotifier notifier;

	DefaultUnauthenticatedExceptionHandler handler;

	@Before
	public void setup() {
		handler = new DefaultUnauthenticatedExceptionHandler(notifier);
	}

	@Test
	public void notify_() {

		// given

		// when
		handler.invoke();
		// then
		verify(notifier).notifyError(DescriptionKey.You_have_not_logged_in, Notification.Type.ERROR_MESSAGE);

	}

}
