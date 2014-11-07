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
package uk.q3c.krail.base.navigate.sitemap;


/**
 * Implementations of this interface take definitions of {@link MasterSitemap} entries from a file, and load them into
 * the
 * {@link MasterSitemap} when invoked by the {@link SitemapService}. See
 * https://sites.google.com/site/q3cjava/sitemap?pli=1#TOC-The-File-Loader for a description of the file format
 */
public interface FileSitemapLoader extends SitemapLoader {

    static String VIEW_DOES_NOT_IMPLEMENT_V7VIEW = "{0} is not a valid V7View class";
    static String VIEW_NOT_FOUND_IN_SPECIFIED_PACKAGES = "View class {0} has not been defined in any of the specified" +
            " packages";
    static String SECTION_NOT_VALID_FOR_SITEMAP = "Invalid section '{0}' in site map file, " +
            "this section has been ignored. Only sections {1} are allowed.";
    static String SECTION_MISSING = "The sitemap file is missing these sections: {0}";
    static String REDIRECT_INVALID = "Invalid redirect line '{0}' ignored";
    static String PROPERTY_MISSING_EQUALS = "Property must contain an '=' sign at line {0} in the {1} section";
    static String PROPERTY_MISSING_KEY = "Property must have a key at line {0} in the {1} section";
    static String PROPERTY_MISSING_VALUE = "Property {0}  cannot have an empty value";
    static String PROPERTY_NAME_UNRECOGNISED = "unrecognised option '{0}' in site map";
    static String LABELKEY_DOES_NOT_IMPLEMENT_I18N_KEY = "{0} does not implement I18NKey";
    static String LABELKEY_NOT_IN_CLASSPATH = "{0} does not exist on the classpath";
    static String LABELKEY_NOT_VALID_CLASS_FOR_I18N_LABELS = "{0} is not a valid class for I18N labels.  It must be " +
            "an enum and implement I18NKey";
    static String SECTION_MISSING_CLOSING = "section requires closing ']' at line {0}";
    static String ENUM_MISSING = "The following enums are missing {0} from {1}";
    static String LINE_FORMAT_INDENTATION_INCORRECT = "Indentation of '{0}' for line {1} is incorrect.  You may not " +
            "get the structure you expect";
    static String LINE_FORMAT_MISSING_START_CHAR = "Line must start with '-', '+', " +
            "'~' or '#' depending on which access control you want, followed by 0..n '-' to indicate indent level, line ";

}
