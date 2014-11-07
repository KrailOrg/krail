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
package uk.q3c.krail.base.shiro;

import com.google.inject.Inject;
import org.apache.shiro.authc.UsernamePasswordToken;
import uk.q3c.krail.base.view.LoginView;
import uk.q3c.krail.i18n.DescriptionKey;

public class DefaultLoginExceptionHandler implements LoginExceptionHandler {

    @Inject
    protected DefaultLoginExceptionHandler() {
    }

    @Override
    public void unknownAccount(LoginView loginView, UsernamePasswordToken token) {
        loginView.setStatusMessage(DescriptionKey.Unknown_Account);
    }

    @Override
    public void incorrectCredentials(LoginView loginView, UsernamePasswordToken token) {
        loginView.setStatusMessage(DescriptionKey.Invalid_Login);
    }

    @Override
    public void expiredCredentials(LoginView loginView, UsernamePasswordToken token) {
        loginView.setStatusMessage(DescriptionKey.Account_Expired);
    }

    @Override
    public void accountLocked(LoginView loginView, UsernamePasswordToken token) {
        loginView.setStatusMessage(DescriptionKey.Account_Locked);
    }

    @Override
    public void excessiveAttempts(LoginView loginView, UsernamePasswordToken token) {
        loginView.setStatusMessage(DescriptionKey.Too_Many_Login_Attempts);
    }

    @Override
    public void concurrentAccess(LoginView loginView, UsernamePasswordToken token) {
        loginView.setStatusMessage(DescriptionKey.Account_Already_In_Use);
    }

    @Override
    public void disabledAccount(LoginView loginView, UsernamePasswordToken token) {
        loginView.setStatusMessage(DescriptionKey.Account_is_Disabled);
    }

}
