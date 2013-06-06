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
package uk.co.q3c.v7.demo.guice;

import java.util.List;

import javax.inject.Singleton;

import uk.co.q3c.v7.base.config.BaseIniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.demo.config.DemoIni;
import uk.co.q3c.v7.demo.config.DemoIniProvider;
import uk.co.q3c.v7.demo.dao.DemoDAOModule;
import uk.co.q3c.v7.demo.ui.DemoUIModule;
import uk.co.q3c.v7.demo.view.DemoModule;
import uk.co.q3c.v7.persist.orient.db.OrientDbModule;

import com.google.inject.Module;

public class DemoGuiceServletInjector extends BaseGuiceServletInjector {

	@Override
	protected void addAppModules(List<Module> baseModules) {
		DemoIni ini = injector.getInstance(DemoIni.class);
		
		baseModules.add(new DemoModule());
		baseModules.add(new DemoDAOModule());
		baseModules.add(new DemoUIModule());
		baseModules.add(new OrientDbModule(ini));
	}
	
	@Override
	protected BaseIniModule createIniModule() {
		return new BaseIniModule(){
			@Override
			protected void bindIni() {
				//FIXME i dont like this double binding, there is a more elegant way ?
				bind(V7Ini.class).toProvider(DemoIniProvider.class).in(Singleton.class);
				bind(DemoIni.class).toProvider(DemoIniProvider.class).in(Singleton.class);
			}
		};
	}
}
