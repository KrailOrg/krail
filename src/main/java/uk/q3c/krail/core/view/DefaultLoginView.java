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
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.ValoTheme;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.SessionBusProvider;
import uk.q3c.krail.core.shiro.LoginExceptionHandler;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.status.UserStatusBusMessage;
import uk.q3c.krail.core.view.component.LoginFormException;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.*;
import uk.q3c.util.ID;

import java.util.Optional;

public class DefaultLoginView extends Grid3x3ViewBase implements LoginView, ClickListener {
    private static Logger log = LoggerFactory.getLogger(DefaultLoginView.class);

    private final LoginExceptionHandler loginExceptionHandler;
    private final Provider<Subject> subjectProvider;
    private final Translate translate;
    private final PubSubSupport<BusMessage> eventBus;
    @Caption(caption = LabelKey.Log_In, description = DescriptionKey.Please_log_in)
    private Panel centrePanel;
    private Label demoInfoLabel;
    private Label demoInfoLabel2;
    @Value(LabelKey.Authentication)
    private Label label;
    @Caption(caption = LabelKey.Password, description = DescriptionKey.Enter_Your_Password)
    private PasswordField passwordBox;
    private Label statusMsgLabel;
    @Caption(caption = LabelKey.Submit, description = DescriptionKey.Submit_Your_Login_Details)
    private Button submitButton;
    @Caption(caption = LabelKey.User_Name, description = DescriptionKey.Enter_your_user_name)
    private TextField usernameBox;

    @Inject
    protected DefaultLoginView(LoginExceptionHandler loginExceptionHandler, SubjectProvider subjectProvider, Translate translate, SessionBusProvider
            eventBusProvider) {
        super();
        this.loginExceptionHandler = loginExceptionHandler;
        this.subjectProvider = subjectProvider;
        this.translate = translate;
        this.eventBus = eventBusProvider.get();
    }

    @SuppressFBWarnings({"FCBL_FIELD_COULD_BE_LOCAL", "FCBL_FIELD_COULD_BE_LOCAL"})
    @Override
    public void doBuild(ViewChangeBusMessage event) {
        super.doBuild(event);
        centrePanel = new Panel();
        centrePanel.addStyleName(ChameleonTheme.PANEL_BUBBLE);
        centrePanel.setSizeUndefined();
        VerticalLayout vl = new VerticalLayout();
        centrePanel.setContent(vl);
        vl.setSpacing(true);
        vl.setSizeUndefined();
        label = new Label();
        usernameBox = new TextField();
        passwordBox = new PasswordField();

        demoInfoLabel = new Label("for this demo, enter any user name, and a password of 'password'");
        demoInfoLabel2 = new Label("In a real application your Shiro Realm implementation defines how to authenticate");

        submitButton = new Button();
        submitButton.addClickListener(this);

        statusMsgLabel = new Label("Please enter your username and password");

        vl.addComponent(label);
        vl.addComponent(demoInfoLabel);
        vl.addComponent(demoInfoLabel2);
        vl.addComponent(usernameBox);
        vl.addComponent(passwordBox);
        vl.addComponent(submitButton);
        vl.addComponent(statusMsgLabel);

        setMiddleCentre(centrePanel);


    }

    @Override
    protected void setIds() {
        super.setIds();
        submitButton.setId(ID.getId(Optional.empty(), this, submitButton));
        submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        submitButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        usernameBox.setId(ID.getId(Optional.of("username"), this, usernameBox));
        passwordBox.setId(ID.getId(Optional.of("password"), this, passwordBox));
        statusMsgLabel.setId(ID.getId(Optional.of("status"), this, statusMsgLabel));
    }


    @Override
    public void buttonClick(ClickEvent event) {
        String username = usernameBox.getValue();
        String password = passwordBox.getValue();
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

    @Override
    public void setUsername(String username) {
        usernameBox.setValue(username);
    }

    @Override
    public void setPassword(String password) {
        passwordBox.setValue(password);
    }

    @Override
    public Button getSubmitButton() {
        return submitButton;
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

    public TextField getUsernameBox() {
        return usernameBox;
    }

    public PasswordField getPasswordBox() {
        return passwordBox;
    }


    @Override
    public String viewName() {

        return getClass().getSimpleName();
    }


}
