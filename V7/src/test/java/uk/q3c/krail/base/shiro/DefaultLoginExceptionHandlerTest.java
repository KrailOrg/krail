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

package uk.q3c.krail.base.shiro;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.base.navigate.Navigator;
import uk.q3c.krail.base.view.LoginView;
import uk.q3c.krail.i18n.DescriptionKey;

import static org.mockito.Mockito.verify;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultLoginExceptionHandlerTest {

    // @Inject
    DefaultLoginExceptionHandler handler;

    @Mock
    LoginView loginView;

    @Mock
    Navigator navigator;

    UsernamePasswordToken token;

    @Before
    public void setup() {
        handler = new DefaultLoginExceptionHandler();
    }

    @Test
    public void accountLocked() {
        // given
        token = new UsernamePasswordToken("fred", "password");
        // when
        handler.accountLocked(loginView, token);
        // then
        verify(loginView).setStatusMessage(DescriptionKey.Account_Locked);

    }

    @Test
    public void unknownAccount() {
        // given
        token = new UsernamePasswordToken("fred", "password");
        // when
        handler.unknownAccount(loginView, token);
        // then
        verify(loginView).setStatusMessage(DescriptionKey.Unknown_Account);

    }

    @Test
    public void concurrentAccess() {
        // given
        token = new UsernamePasswordToken("fred", "password");
        // when
        handler.concurrentAccess(loginView, token);
        // then
        verify(loginView).setStatusMessage(DescriptionKey.Account_Already_In_Use);

    }

    @Test
    public void disabledAccount() {
        // given
        token = new UsernamePasswordToken("fred", "password");
        // when
        handler.disabledAccount(loginView, token);
        // then
        verify(loginView).setStatusMessage(DescriptionKey.Account_is_Disabled);
    }

    @Test
    public void excessiveAttempts() {
        // given
        token = new UsernamePasswordToken("fred", "password");
        // when
        handler.excessiveAttempts(loginView, token);
        // then
        verify(loginView).setStatusMessage(DescriptionKey.Too_Many_Login_Attempts);
    }

    @Test
    public void expiredCredentials() {
        // given
        token = new UsernamePasswordToken("fred", "password");
        // when
        handler.expiredCredentials(loginView, token);
        // then
        verify(loginView).setStatusMessage(DescriptionKey.Account_Expired);
    }

    @Test
    public void incorrectCredentials() {
        // given
        token = new UsernamePasswordToken("fred", "password");
        // when
        handler.incorrectCredentials(loginView, token);
        // then
        verify(loginView).setStatusMessage(DescriptionKey.Invalid_Login);
    }
}
