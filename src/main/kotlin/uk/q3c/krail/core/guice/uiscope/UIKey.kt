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
package uk.q3c.krail.core.guice.uiscope

import java.io.Serializable
import java.util.*
import javax.annotation.concurrent.Immutable

/**
 * This class is entirely passive - it is a surrogate for the UI itself during the IoC process in support of
 * [UIScoped]. <br></br>
 * <br></br>
 * The UI instance would normally be used as the key in @link [UIScope], but this causes a problem with
 * constructor injection of a UI instance. This is because any constructor parameters which are also UIScoped are
 * created before the UI, and therefore before the UI entry in UIScope exists. To overcome this, the UI is represented
 * by a [UIKey], which is available from the start of UI construction. The UI itself, and any UIScoped injections
 * are then linked by that [UIKey] instance.<br></br>
 * <br></br>
 * The counter value is set by the [UIKeyProvider]
 */
@Immutable
data class UIKey @JvmOverloads constructor(val uuid: UUID = UUID.randomUUID()) : Comparable<UIKey>, Serializable {

    override fun toString(): String {
        return "UIKey:$uuid"
    }

    override fun compareTo(other: UIKey): Int {
        return this.uuid.compareTo(other.uuid)
    }

}
