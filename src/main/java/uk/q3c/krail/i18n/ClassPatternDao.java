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

package uk.q3c.krail.i18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * An interface for I18N Patterns accessed through Class based methods (using {@link EnumResourceBundle}).  The Guice binding is identified by {@link
 * ClassPatternSource}
 * <p>
 * Created by David Sowerby on 28/07/15.
 */
public interface ClassPatternDao extends PatternDao {


    /**
     * Returns the text file being used to write out key-value entries.  May be null if not set
     */
    @Nullable
    File getWriteFile();

    /**
     * Set the text file to which you want the {@link #write(PatternCacheKey, String)} method to append a key-value entry
     *
     * @param writeFile
     *         the text file to which you want the {@link #write(PatternCacheKey, String)} method to append a key-value entry
     */
    void setWriteFile(@Nonnull File writeFile);
}
