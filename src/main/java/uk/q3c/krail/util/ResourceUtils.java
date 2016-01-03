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

package uk.q3c.krail.util;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Created by David Sowerby on 03 Jan 2016
 */
public interface ResourceUtils {

    /**
     * Returns the base directory path for the application if there is a VaadinService is running, or throws a {@link
     * IllegalStateException} if no service is running
     *
     * @return the base directory path for the application
     */
    String applicationBasePath();

    /**
     * Returns the base directory path for the application if there is a VaadinService is running, or throws a {@link
     * IllegalStateException} if no service is running
     *
     * @return File representing the base directory path for the application
     */
    File applicationBaseDirectory();

    /**
     * a convenience method creating a {@link File} object referencing {user.home}/temp
     *
     * @return a {@link File} object referencing {user.home}/temp
     */
    File userTempDirectory();

    /**
     * a convenience method equivalent to creating a {@link File} object using the System property 'user.home'
     *
     * @return a {@link File} object using the System property 'user.home'
     */
    File userHomeDirectory();

    /**
     * The same as {@link #webInfDirectory()}
     */
    File configurationDirectory();

    /**
     * Returns a File object for the WEB-INF directory
     *
     * @return a File object for the WEB-INF directory
     */
    File webInfDirectory();

    /**
     * Returns a File object for a file name resident within the resources directory
     *
     * @param fileName the name of the file
     * @return a File object for a file name resident within the resources directory
     */
    File resourcePath(@Nonnull String fileName);
}
