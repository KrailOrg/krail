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

public class Descriptions_de extends Descriptions {

	private static ImmutableMap<DescriptionKey, String> map;
	static {

		map = new ImmutableMap.Builder<DescriptionKey, String>()
				// @formatter:off
				.put(DescriptionKey.Last_Name,"Der Nachname oder der Familienname")
				.put(DescriptionKey.Confirm_Ok, "Bestätigen Sie, dass dieser Wert in Ordnung ist")
				.put(DescriptionKey.Account_Already_In_Use,"Dieses Konto ist bereits in Verwendung. Sie müssen sich ausloggen bevor Sie sich wieder einloggen können.")
				.put(DescriptionKey.Account_is_Disabled,"Das Konto ist deaktiviert")
				.put(DescriptionKey.Account_Expired,"Ihr Konto ist abgelaufen")
				.put(DescriptionKey.Account_Locked,"Ihr Konto ist gesperrt")
				.put(DescriptionKey.Application_Configuration_Service,"Dieser Service lädt die Anwendungs-Konfiguration aus V7.ini")
				.put(DescriptionKey.Enter_your_user_name,"Geben Sie ihren Benutzernamen ein")
				.put(DescriptionKey.Invalid_Login,"ungültiger Login")
				.put(DescriptionKey.No_Permission,"Sie haben keine Berechtigung für diese Aktion")
				.put(DescriptionKey.Please_log_in,"Bitte loggen Sie sich ein")
				.put(DescriptionKey.Select_from_available_languages,"Wählen Sie aus den verfügbaren Sprachen aus")
				.put(DescriptionKey.Sitemap_Service,"Sitemap Service")
				.put(DescriptionKey.Too_Many_Login_Attempts,"zuviele Login-Versuche. Diese Konto muss entsperrt werden!")
				.put(DescriptionKey.Unknown_Account,"Ihr Benutzername und Passwort ist unbekannt")
				.put(DescriptionKey.You_have_not_logged_in,"Sie sind nicht eingeloggt")				
				.build();
				// @formatter:on

	}

	@Override
	public ImmutableMap<DescriptionKey, String> getMap() {
		return map;
	}

}
