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

package uk.q3c.krail.core.push;

import uk.q3c.krail.core.eventbus.BusMessage;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by David Sowerby on 26/05/15.
 */
public class PushMessage implements BusMessage {
    private final String group;
    private final String message;

    public PushMessage(@Nonnull String group,@Nonnull String message) {
        checkNotNull(group);
        checkNotNull(message);
        this.group = group;
        this.message = message;
    }

    public String getGroup() {
        return group;
    }

    public String getMessage() {
        return message;
    }
}
