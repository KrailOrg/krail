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
package uk.co.q3c.v7.base.notify;

import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.MessageKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.multibindings.MapBinder;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class })
@SuppressWarnings("rawtypes")
public class DefaultUserNotifierTest {

	@Inject
	Map<I18NKey, ErrorNotification> errorNotifications;
	@Inject
	Map<I18NKey, WarningNotification> warningNotifications;
	@Inject
	Map<I18NKey, InformationNotification> informationNotifications;

	@Mock
	ErrorNotification errorNotification1;

	@Mock
	ErrorNotification errorNotification2;

	@Mock
	WarningNotification warningNotification1;

	@Mock
	WarningNotification warningNotification2;

	@Mock
	InformationNotification informationNotification1;

	@Mock
	InformationNotification informationNotification2;

	@Inject
	DefaultUserNotifier dun;

	@Inject
	Translate translate;

	@Test
	public void message() {

		// given

		// when
		dun.notifyError(MessageKey.Service_not_Started, "error");
		dun.notifyInformation(MessageKey.Service_not_Started, "info");
		dun.notifyWarning(MessageKey.Service_not_Started, "warn");
		// then
		String errorMsg = translate.from(MessageKey.Service_not_Started, "error");
		verify(errorNotification1).message(errorMsg);
		verify(errorNotification2).message(errorMsg);
		String warnMsg = translate.from(MessageKey.Service_not_Started, "warn");
		verify(warningNotification1).message(warnMsg);
		verify(warningNotification2).message(warnMsg);
		String infoMsg = translate.from(MessageKey.Service_not_Started, "info");
		verify(informationNotification1).message(infoMsg);
		verify(informationNotification2).message(infoMsg);
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				MapBinder<I18NKey, ErrorNotification> errorNotificationBinder = MapBinder.newMapBinder(binder(),
						I18NKey.class, ErrorNotification.class);
				MapBinder<I18NKey, WarningNotification> warningNotificationBinder = MapBinder.newMapBinder(binder(),
						I18NKey.class, WarningNotification.class);
				MapBinder<I18NKey, InformationNotification> informationNotificationBinder = MapBinder.newMapBinder(
						binder(), I18NKey.class, InformationNotification.class);
				errorNotificationBinder.addBinding(LabelKey.Splash).toInstance(errorNotification1);
				warningNotificationBinder.addBinding(LabelKey.Splash).toInstance(warningNotification1);
				informationNotificationBinder.addBinding(LabelKey.Splash).toInstance(informationNotification1);

				errorNotificationBinder.addBinding(LabelKey.Message_Bar).toInstance(errorNotification2);
				warningNotificationBinder.addBinding(LabelKey.Message_Bar).toInstance(warningNotification2);
				informationNotificationBinder.addBinding(LabelKey.Message_Bar).toInstance(informationNotification2);
			}

		};
	}

}
