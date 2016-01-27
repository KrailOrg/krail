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

import static uk.q3c.krail.core.i18n.MessageKey.*;

/**
 * The base for the resource bundle of {@link Messages}. The separation between them is arbitrary, but helps break down
 * what could other wise be long lists, and only one of them needs to look up parameter values:
 * <ol>
 * <li>{@link Labels} : short, usually one or two words, no parameters, generally used as captions
 * <li>{@link Descriptions} : longer, typically several words, no parameters, generally used in tooltips
 * <li>{@link Messages} : contains parameters, typically used for user messages.
 *
 * @author David Sowerby 3 Aug 2013
 */
public class Messages extends EnumResourceBundle<MessageKey> {


    public Messages() {
        super();
    }

    @Override
    protected void loadMap() {
        put(Invalid_URI, "{0} is not a valid page");
        put(Service_not_Started, "You cannot use service {0} until it has been started");
        put(Locale_Change, "Language and Country changed to {0}");
        //use with params Bundle_Path and source at {0}{1}
        put(Use_Key_Path, "If this option is true, the bundle name for source '{1}' is appended to the package path of the sample key, otherwise the path set" +
                " by {0} is used.");
        //use params Use_key_Path and source at {0}{1}
        put(Bundle_Path, "The path in which to find the bundle, not used if option {0} is true, for source: '{1}'");
        // param {1} is Option_Stub_Value
        put(Option_Stub_with_Key_Name, "If true, stubs for source '{0}' are generated using the key name, otherwise {1} is used");
        //param {0} is Option_Stub_with_Key_Name
        put(Option_Stub_Value, "If {0} is false, stubs for source '{1}' are generated using the key name");
        put(Option_Auto_Stub, "If true, and no value is found in source '{0}', create a stub using stub value options");
        //param {0] is
        put(Option_Source_Order_Default, "The default source order to be used for a bundle, unless overridden by {0}");
        put(Option_Source_Order, "The source order to be used for bundle: {0}");
        put(Button_is_Visible, "The {0} button is visible");
        put(I18NKey_export_failed, "The export of I18N Keys failed with an exception.  The exception message was:\n\n {0} ");
        put(Invalid_Locale_Langugage_Tag, "'{0}' is not a valid Locale language tag.  See Locale.Builder().setLanguageTag()");
        put(Keys_exported, "{0} keys were exported across {1} locales ");
        put(Setup_I18NKey_export, "List the Locales you want to export below, then press {0}. \n\n Note that no check is made that the Locales you select are" + " supported by your application - this is to allow the export before you configure the supported Locales.");
        put(All_Keys_exported, "All the Krail core keys will be exported - LabelKey, DescriptionKey, MessageKey and ValidationKey");
    }


}
