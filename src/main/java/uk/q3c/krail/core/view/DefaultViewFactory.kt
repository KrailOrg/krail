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
package uk.q3c.krail.core.view

import com.google.inject.Inject
import com.google.inject.Key
import com.google.inject.TypeLiteral
import org.slf4j.LoggerFactory
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import java.io.IOException
import java.io.ObjectInputStream

class DefaultViewFactory @Inject constructor(
        private val serializationSupport: SerializationSupport,
        private val injectorLocator: InjectorLocator)

    : ViewFactory {
    private var log = LoggerFactory.getLogger(DefaultViewFactory::class.java)

    override fun <T : KrailView> get(viewClass: Class<T>): T {
        val typeLiteral = TypeLiteral.get(viewClass)
        val key = Key.get(typeLiteral)
        log.debug("getting or retrieving instance of {}", viewClass)
        val view = injectorLocator.get().getInstance(key)
        log.debug("Calling view.init()")
        view.init()
        return view
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        log = LoggerFactory.getLogger(DefaultViewFactory::class.java)
        serializationSupport.deserialize(this)
    }
}
