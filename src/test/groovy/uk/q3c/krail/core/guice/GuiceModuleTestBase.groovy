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

package uk.q3c.krail.core.guice

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import spock.lang.Specification

/**
 * Integration test for {@link DefaultServletContextListener}
 *
 * Created by David Sowerby on 22/07/15.
 */
abstract class GuiceModuleTestBase extends Specification {

    Injector injector

    protected Injector createInjector(Module moduleUnderTest) {
        List<Module> modules = new ArrayList<>()
        addSupportingModules(modules)
        modules.add(moduleUnderTest)
        return Guice.createInjector(modules)

    }

    abstract List<Module> addSupportingModules(List<Module> modules)

    def getBinding(typeLiteral) {
        //noinspection GroovyAssignabilityCheck
        Key<?> key = Key.get(typeLiteral)
        def binding = injector.getBinding(key)
        binding.provider.get()
    }

    def getBinding(typeLiteral, annotationClass) {
        def key = Key.get(typeLiteral, annotationClass)
        def binding = injector.getBinding(key)
        binding.provider.get()
    }
}
