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
package uk.q3c.krail.core.navigate.sitemap;

/**
 * The German language resource bundle for {@link StandardPageKey}
 *
 * @author David Sowerby 9 Feb 2013
 */
public class StandardPageLabels_de extends StandardPageLabels {


    @Override
    protected void loadMap(Class<Enum<?>> enumKeyClass) {
        put(StandardPageKey.Public_Home, "Öffentliche Startseite");
        put(StandardPageKey.Log_In, "Einloggen");
        put(StandardPageKey.Log_Out, "Ausloggen");
        put(StandardPageKey.Private_Home, "Privat Startseite");
    }


}
