/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.data;

/**
 * Implementations provide a configuration object for an instance of a particular data source (configured by an implementation of {@link
 * DataSourceConfiguration}) - where instance would be 'DEV', 'TEST', 'PROD' etc.  This is a logical instance - the source itself may be clustered or
 * distributed but considered by the application as a single instance.
 * <p>
 * Created by David Sowerby on 03/04/15.
 *
 * @param <C>
 *         the implementation (used for fluent API)
 */
public interface DataSourceInstanceConfiguration<C> {

    enum LoggingLevel {OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL}

    /**
     * the url for the instance
     *
     * @return the url for the instance
     */
    String getConnectionUrl();

    /**
     * the user name for this instance
     *
     * @return the user name for this instance
     */
    String getUser();

    /**
     * the password for this instance
     *
     * @return the password for this instance
     */
    String getPassword();

    /**
     * Auto create table(s) for this instance if they do not already exist.  Only relevant for implementations which use a schema
     *
     * @return true if tables will be auto-created
     */
    boolean isAutoCreate();

    /**
     * Set the url for the instance
     *
     * @param url
     *         the url for the instance
     *
     * @return this
     */
    C url(String url);

    /**
     * Set the user name for this instance
     *
     * @param user
     *         the user name for this instance
     *
     * @return this
     */
    C user(String user);

    /**
     * Set the password for this instance
     *
     * @param password
     *         the new password
     *
     * @return this
     */
    C password(String password);

    /**
     * Auto create table(s) for this instance if they do not already exist.  Only relevant for implementations which use a schema.  Default is false;
     *
     * @param autoCreate
     *         set to true if you want tables auto-created
     *
     * @return this
     */
    C autoCreate(boolean autoCreate);

    LoggingLevel getLoggingLevel();
}
