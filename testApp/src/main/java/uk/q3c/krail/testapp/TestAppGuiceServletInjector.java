/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.testapp;

import com.google.inject.Module;
import uk.q3c.krail.core.guice.BaseGuiceServletInjector;
import uk.q3c.krail.core.navigate.sitemap.SystemAccountManagementPages;
import uk.q3c.krail.testapp.i18n.TestAppI18NModule;
import uk.q3c.krail.testapp.view.TestAppPages;
import uk.q3c.krail.testapp.view.TestAppViewModule;

import java.util.List;

//@WebListener
public class TestAppGuiceServletInjector extends BaseGuiceServletInjector {

    @Override
    protected void addAppModules(List<Module> modules) {
        modules.add(new TestAppUIModule());
    }

    @Override
    protected Module viewModule() {
        return new TestAppViewModule();
    }

    @Override
    protected void addSitemapModules(List<Module> baseModules) {
        super.addSitemapModules(baseModules);
        baseModules.add(new SystemAccountManagementPages());
        baseModules.add(new TestAppPages());
    }

    @Override
    protected Module servletModule() {
        return new TestAppServletModule();
    }

    @Override
    protected Module i18NModule() {
        return new TestAppI18NModule();
    }

}
