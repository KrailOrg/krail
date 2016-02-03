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
package testutil;

import com.google.inject.Module;
import testutil.dummy.DummyModule;
import uk.q3c.krail.core.guice.DefaultBindingManager;
import uk.q3c.krail.core.option.InMemory;
import uk.q3c.krail.core.option.OptionModule;

import java.util.List;

public class TestBindingManager extends DefaultBindingManager {

    private boolean addAppModulesCalled=false;

    public TestBindingManager() {
        super();
    }

    @Override
    protected void addAppModules(List<Module> modules) {
        modules.add(new DummyModule());
        addAppModulesCalled = true;
    }

    public boolean isAddAppModulesCalled() {
        return addAppModulesCalled;
    }

    /**
     * Override this if you have provided your own {@link OptionModule}.
     *
     * @return module instance
     */
    @Override
    protected Module optionModule() {
        return new OptionModule().activeSource(InMemory.class);
    }
}
