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
package uk.q3c.krail.i18n;

/**
 * The base for the resource bundle of Labels for Locale de. Entry is purely for testing
 *
 * @author David Sowerby 9 Feb 2013
 */
public class Labels_it extends Labels {

    @Override
    protected void loadMap(Class<Enum<?>> enumKeyClass) {
        put(LabelKey.Cancel, "Cancellare");
        put(LabelKey.Enable_Account, "Abilita Account");
        put(LabelKey.Error, "Errore");
        put(LabelKey.First_Name, "Nome");
        put(LabelKey.Guest, "Ospite");
        put(LabelKey.Last_Name, "Cognome");
        put(LabelKey.Log_In, "Log in");
        put(LabelKey.Message_Box, "Scatola di Messaggio");
        put(LabelKey.Notifications, "Notifiche");
        put(LabelKey.No, "No");
        put(LabelKey.Push, "Spinta");
        put(LabelKey.Refresh_Account, "Aggiorna Conto");
        put(LabelKey.Request_Account, "Richiesta Conto");
        put(LabelKey.Reset_Account, "Ripristina Conto");
        put(LabelKey.System_Account, "Conto di Sistema");
        put(LabelKey.Small, "Piccolo");
        put(LabelKey.Yes, "Sì");
    }


}
