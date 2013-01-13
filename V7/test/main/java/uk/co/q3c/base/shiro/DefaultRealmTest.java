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
package uk.co.q3c.base.shiro;

import static org.fest.assertions.Assertions.*;

import javax.inject.Inject;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.shiro.AlwaysPasswordCredentialsMatcher;
import uk.co.q3c.v7.base.shiro.DefaultLoginAttemptLog;
import uk.co.q3c.v7.base.shiro.DefaultRealm;
import uk.co.q3c.v7.base.shiro.LoginAttemptLog;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext()
public class DefaultRealmTest {

	String onlyValidPassword = "password";

	@Inject
	Realm realm;

	@Inject
	LoginAttemptLog attemptLog;

	@Test
	public void validPassword() {

		// given
		// when
		AuthenticationInfo info = realm.getAuthenticationInfo(token("fred", onlyValidPassword));

		// then
		assertThat(info).isNotNull();

	}

	@Test
	public void passwordFail() {

		// given
		attemptLog.clearUnsuccessful("fred");
		// when
		AuthenticationInfo info = realm.getAuthenticationInfo(token("fred", "rubbish"));
		// then
		assertThat(info).isNull();

	}

	@Test(expected = ExcessiveAttemptsException.class)
	public void bombsAfter3Fails() {

		// given

		// when
		AuthenticationInfo info = realm.getAuthenticationInfo(token("fred", "rubbish"));
		info = realm.getAuthenticationInfo(token("fred", "rubbish"));
		info = realm.getAuthenticationInfo(token("fred", "rubbish"));
		// then
		// exception expected

	}

	private UsernamePasswordToken token(String username, String password) {
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		return token;
	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(CredentialsMatcher.class).to(AlwaysPasswordCredentialsMatcher.class);
				bind(LoginAttemptLog.class).to(DefaultLoginAttemptLog.class);
				bind(Realm.class).to(DefaultRealm.class);

			}

		};
	}

}
