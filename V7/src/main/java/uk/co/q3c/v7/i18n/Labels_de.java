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
package uk.co.q3c.v7.i18n;

import com.google.common.collect.ImmutableMap;

/**
 * The base for the resource bundle of Labels for Locale de. Entry is purely for testing
 *
 *
 * @author David Sowerby 9 Feb 2013
 *
 */
public class Labels_de extends Labels {

	private static final ImmutableMap<LabelKey, String> map;
	static {
		map = new ImmutableMap.Builder<LabelKey, String>()
				// @formatter:off

				.put(LabelKey.Cancel, "Stornieren")
				.put(LabelKey.Enable_Account, "Konto Aktivieren")
				.put(LabelKey.Error, "Fehler")
				.put(LabelKey.First_Name, "Vorname")
				.put(LabelKey.Guest, "Gast")
				.put(LabelKey.Home_Page, "Startseite")
				.put(LabelKey.Last_Name, "Nachname")
				.put(LabelKey.Log_In, "Einloggen")
				.put(LabelKey.Message_Box, "Nachricht Feld")
				.put(LabelKey.No, "Nein")
				.put(LabelKey.Notifications, "Benachrichtigungen")
				.put(LabelKey.Private, "Privat")
				.put(LabelKey.Public, "Öffentlichkeit")
				.put(LabelKey.Push, "Druck")
				.put(LabelKey.Refresh_Account, "Aktualisieren Konto")
				.put(LabelKey.Request_Account, "Konto Anfordern")
				.put(LabelKey.Reset_Account, "Konto Zurücksetzen")
				.put(LabelKey.System_Account, "Systemkonto")
				.put(LabelKey.Small, "Klein")
				.put(LabelKey.Yes, "Ja")
				.build();

		// @formatter:on
	}

	@Override
	public ImmutableMap<LabelKey, String> getMap() {
		return map;
	}

}
