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
        super(DescriptionKey.class);
    }

    @Override
    protected void loadMap() {
        put(DescriptionKey.Last_Name, "the last name or family name");
        put(DescriptionKey.Last_Name, "the last name or family name");
        put(DescriptionKey.Confirm_Ok, "Confirm this Value is Ok");
        put(DescriptionKey.No_Permission, "You do not have permission for that action");
        put(DescriptionKey.Application_Configuration_Service, "This service loads the application " + "configuration " +
                "from krail.ini");
        put(DescriptionKey.Sitemap_Service, "This service creates the Sitemap using options from the " + "application" +
                " configuration");
        put(DescriptionKey.Unknown_Account, "That username or password was not recognised");
        put(DescriptionKey.Account_Expired, "Your account has expired");
        put(DescriptionKey.Account_Already_In_Use, "This account is already in use.  You must log out of " + "that " +
                "session before you can log in again.");
        put(DescriptionKey.Account_Locked, "Your account is locked");
        put(DescriptionKey.Too_Many_Login_Attempts, "Login has failed too many times, the account will " + "need to " +
                "be reset");
    }


}
