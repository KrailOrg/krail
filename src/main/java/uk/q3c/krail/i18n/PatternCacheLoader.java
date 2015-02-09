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

import java.util.List;

/**
 * Created by David Sowerby on 08/12/14.
 */
public interface PatternCacheLoader {

    /**
     * Returns the order in which sources are processed - the first which returns a valid value for a key is used.  The
     * way in which the order is decided is determined is defined by the implementation
     *
     * @param key
     *
     * @return
     */
    List<String> bundleSourceOrder(I18NKey key);

    List<String> getOptionReaderOrder(String baseName);

    List<String> getOptionReaderOrderDefault();

    void setOptionReaderOrderDefault(String... sources);

    void setOptionReaderOrder(String baseName, String... sources);

    void setOptionAutoStub(boolean autoStub, String source);

    void setOptionStubWithKeyName(boolean useKeyName, String source);

    void setOptionStubValue(String stubValue, String source);
}
