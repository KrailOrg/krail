/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.i18n;

/**
 * The base for the resource bundle of LoginLabels for Locale de. Entry is purely for testing
 *
 * @author David Sowerby 9 Feb 2013
 */
public class CommonLabels_it extends CommonLabels {

    @Override
    protected void loadMap() {
        put(CommonLabelKey.Cancel, "Cancellare");
//        put(CommonLabelKey.Enable_Account, "Abilita Account");
        put(CommonLabelKey.Error, "Errore");
//        put(CommonLabelKey.First_Name, "Nome");
//        put(CommonLabelKey.Guest, "Ospite");
//        put(CommonLabelKey.Last_Name, "Cognome");
//        put(CommonLabelKey.Message_Box, "Scatola di Messaggio");
        put(CommonLabelKey.Notifications, "Notifiche");
        put(CommonLabelKey.No, "No");
        put(CommonLabelKey.Push, "Spinta");
//        put(CommonLabelKey.Refresh_Account, "Aggiorna Conto");
//        put(CommonLabelKey.Request_Account, "Richiesta Conto");
//        put(CommonLabelKey.Reset_Account, "Ripristina Conto");
//        put(CommonLabelKey.System_Account, "Conto di Sistema");
        put(CommonLabelKey.Small, "Piccolo");
        put(CommonLabelKey.Yes, "Sì");
    }


}
