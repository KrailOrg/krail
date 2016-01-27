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

package uk.q3c.krail.core.user.opt;

import uk.q3c.krail.core.i18n.I18NKey;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Reads the {@link OptionKey}s from a supplied {@link OptionContext}, and provides a popup window to display them to the user.
 * <p>
 * Created by David Sowerby on 29/05/15.
 */
public interface OptionPopup {

    /**
     * Returns a map of {@link OptionKey} to field type, which provide enough information for an option selection form to be displayed (name, description,
     * value)
     *
     * @param context
     *         the context to get the keys from
     *
     * @return a map of {@link OptionKey} to field type, which provide enough information for an option selection form to be displayed (name, description,
     * value)
     */
    @Nonnull
    Map<OptionKey, Class<?>> contextKeys(OptionContext context);

    /**
     * Pops up a window containing fields for the user to view / set values for all the keys from {@link #contextKeys(OptionContext)}
     */
    void popup(@Nonnull OptionContext context, I18NKey windowCaption);
}
