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

package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ConcurrentAccessException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBusProvider;
import uk.q3c.krail.core.i18n.Caption;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Value;
import uk.q3c.krail.core.shiro.LoginExceptionHandler;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.status.UserStatusBusMessage;
import uk.q3c.krail.core.view.component.AssignComponentId;
import uk.q3c.krail.core.view.component.LoginFormException;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

public class DefaultLoginView extends Grid3x3ViewBase implements LoginView, ClickListener {
    private static Logger log = LoggerFactory.getLogger(DefaultLoginView.class);

    private final LoginExceptionHandler loginExceptionHandler;
    private final Provider<Subject> subjectProvider;
    private final Translate translate;
    private final PubSubSupport<BusMessage> eventBus;
    @AssignComponentId(assign = false, drilldown = false)
    @Caption(caption = LabelKey.Log_In, description = DescriptionKey.Please_log_in)
    private Panel centrePanel;
    @Value(LabelKey.Authentication)
    private Label label;
    @Caption(caption = LabelKey.Password, description = DescriptionKey.Enter_Your_Password)
    private PasswordField password;
    private Label statusMsgLabel;
    @Caption(caption = LabelKey.Submit, description = DescriptionKey.Submit_Your_Login_Details)
    private Button submit;
    @Caption(caption = LabelKey.User_Name, description = DescriptionKey.Enter_your_user_name)
    private TextField username;

    @Inject
    protected DefaultLoginView(LoginExceptionHandler loginExceptionHandler, SubjectProvider subjectProvider, Translate translate, SessionBusProvider
            eventBusProvider) {
        super(translate);
        this.loginExceptionHandler = loginExceptionHandler;
        this.subjectProvider = subjectProvider;
        this.translate = translate;
        this.eventBus = eventBusProvider.get();
        nameKey = LabelKey.Log_In;
        descriptionKey = DescriptionKey.Log_In;
    }

    @Override
    public void doBuild(ViewChangeBusMessage event) {
        super.doBuild(event);
        centrePanel = new Panel();
        centrePanel.addStyleName(ValoTheme.PANEL_WELL);
        centrePanel.setSizeUndefined();
        VerticalLayout vl = new VerticalLayout();
        centrePanel.setContent(vl);
        vl.setSpacing(true);
        vl.setSizeUndefined();
        label = new Label();
        username = new TextField();
        password = new PasswordField();

        Label demoInfoLabel = new Label("for this demo, enter any user name, and a password of 'password'");
        Label demoInfoLabel2 = new Label("In a real application your Shiro Realm implementation defines how to authenticate");

        submit = new Button();
        submit.addClickListener(this);
        submit.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        submit.addStyleName(ValoTheme.BUTTON_PRIMARY);

        statusMsgLabel = new Label("Please enter your username and password");

        vl.addComponent(label);
        vl.addComponent(demoInfoLabel);
        vl.addComponent(demoInfoLabel2);
        vl.addComponent(username);
        vl.addComponent(password);
        vl.addComponent(submit);
        vl.addComponent(statusMsgLabel);

        setMiddleCentre(centrePanel);


    }


    @Override
    public void buttonClick(ClickEvent event) {
        String username = this.username.getValue();
        String password = this.password.getValue();
        if (StringUtils.isEmpty(username)) {
            throw new LoginFormException(LabelKey.Username_Cannot_be_Empty);
        }
        if (StringUtils.isEmpty(password)) {
            throw new LoginFormException(LabelKey.Password_Cannot_be_Empty);
        }
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subjectProvider.get()
                           .login(token);
            log.debug("Publishing UserStatusBusMessage from: '{}'", this.getClass()
                                                                        .getSimpleName());
            eventBus.publish(new UserStatusBusMessage(this, true));
        } catch (UnknownAccountException uae) {
            loginExceptionHandler.unknownAccount(this, token);
        } catch (IncorrectCredentialsException ice) {
            loginExceptionHandler.incorrectCredentials(this, token);
        } catch (ExpiredCredentialsException ece) {
            loginExceptionHandler.expiredCredentials(this, token);
        } catch (LockedAccountException lae) {
            loginExceptionHandler.accountLocked(this, token);
        } catch (ExcessiveAttemptsException excess) {
            loginExceptionHandler.excessiveAttempts(this, token);
        } catch (DisabledAccountException dae) {
            loginExceptionHandler.disabledAccount(this, token);
        } catch (ConcurrentAccessException cae) {
            loginExceptionHandler.concurrentAccess(this, token);
        } catch (AuthenticationException ae) {
            loginExceptionHandler.authentication(this, token);
        }
        // unexpected condition - error?
        // an exception would be raised if login failed
    }

    @Deprecated // use getSubmit()
    public Button getSubmitButton() {
        return submit;
    }

    @Override
    @Deprecated // use getSubmit()
    public Button getSubmit() {
        return submit;
    }

    @Override
    public TextField getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username.setValue(username);
    }

    @Override
    public String getStatusMessage() {
        return statusMsgLabel.getValue();
    }

    @Override
    public void setStatusMessage(I18NKey messageKey) {
        setStatusMessage(translate.from(messageKey));
    }

    @Override
    public void setStatusMessage(String msg) {
        statusMsgLabel.setValue(msg);
    }

    @Override
    public PasswordField getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password.setValue(password);
    }

    @Deprecated // use getUsername
    public TextField getUsernameBox() {
        return username;
    }

    @Deprecated // user getPassword
    public PasswordField getPasswordBox() {
        return password;
    }




}
