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
package uk.co.q3c.v7.demo.i18n;

import com.google.common.collect.ImmutableMap;
import uk.co.q3c.v7.i18n.Labels;
import uk.co.q3c.v7.i18n.MapResourceBundle;
import uk.co.q3c.v7.i18n.Messages;

/**
 * The base for the resource bundle of {@link Descriptions}. The separation between them is arbitrary, but helps break
 * down what could other wise be long lists, and only one of them needs to look up parameter values:
 * <ol>
 * <li>{@link Labels} : short, usually one or two words, no parameters, generally used as captions
 * <li>{@link Descriptions} : longer, typically several words, no parameters, generally used in tooltips
 * <li>{@link Messages} : contains parameters, typically used for user messages.
 *
 * @author David Sowerby 3 Aug 2013
 */
public class Descriptions extends MapResourceBundle<DescriptionKey> {

    private static final ImmutableMap<DescriptionKey, String> map;

    static {
        map = new ImmutableMap.Builder<DescriptionKey, String>()
                // @formatter:off
		.put(DescriptionKey.Notifications, "Vaadin provides " + "<a href=\"https://vaadin" +
                ".com/en_GB/book/vaadin7/-/page/application.notifications.html\" " +
                        "target=\"\">notification</a>" + " 'splash' messages.  V7 adds to this by enabling the use of" +
                " multiple" + " methods of notification, combined or selected for each of the message types - Error, " +
                "" + "Warning and Information, and invoked through a single call to the UserNotifier class.  " +
                "\nConfiguration is through a Guice module (DefaultUserNotificationModule by default)." + "\nWhen you" +
                " try the buttons below, note that the " + "message is presented through both the Vaadin notification" +
                " and the message bar.  " + "For more detail see the " + "<a href=\"https://sites.google" +
                ".com/site/q3cjava/notifications\" target=\"\">V7 " +
                        "documentation</a>")
		.build();
// @formatter:on
    }

    @Override
    public ImmutableMap<DescriptionKey, String> getMap() {
        return map;
    }
}