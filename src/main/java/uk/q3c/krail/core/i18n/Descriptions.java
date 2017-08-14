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

import uk.q3c.krail.i18n.EnumResourceBundle;

import static uk.q3c.krail.core.i18n.DescriptionKey.*;

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
public class Descriptions extends EnumResourceBundle<DescriptionKey> {

    public Descriptions() {
        super();
    }

    @Override
    protected void loadMap() {
        put(Last_Name, "the last name or family name");
        put(Last_Name, "the last name or family name");
        put(Confirm_Ok, "Confirm this Value is Ok");
        put(No_Permission, "You do not have permission for that action");
        put(Application_Configuration_Service, "This service loads the application configuration " +
                "from krail.ini");
        put(Sitemap_Service, "This service creates the Sitemap using options from the application" +
                " configuration");
        put(Unknown_Account, "That username or password was not recognised");
        put(Account_Expired, "Your account has expired");
        put(Account_Already_In_Use, "This account is already in use.  You must log out of " + "that " +
                "session before you can log in again.");
        put(Account_Locked, "Your account is locked");
        put(Too_Many_Login_Attempts, "Login has failed too many times, the account will " + "need to " +
                "be reset");
        put(Authentication_Failed, "Your login attempt failed");
        put(Use_Field_Name_In_Validation_Message, "If true, the field name is included as part of the validation message");
        put(Maximum_Menu_Depth, "The maximum depth you want to display in the menu");
        put(Maximum_Tree_Depth, "The maximum depth you want to display in the tree");
        put(Preferred_Locale, "The preferred Locale for the current user");
        put(Sort_Type, "The sort type to apply");
        put(Sort_Ascending, "If true, sort in ascending order, otherwise in descending order");
        put(Flag_Icon_Size, "The size of the flag icon to use");
        put(Log_out_first, "You will need to log out to do that");
    }


}
