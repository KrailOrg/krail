/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ChameleonTheme;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.shiro.LoginExceptionHandler;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.status.UserStatusBusMessage;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.*;
import uk.q3c.util.ID;

import java.util.Optional;

public class DefaultLoginView extends GridViewBase implements LoginView, ClickListener {
    private final LoginExceptionHandler loginExceptionHandler;
    private final Provider<Subject> subjectProvider;
    private final Translate translate;
    private final PubSubSupport<BusMessage> eventBus;
    @Caption(caption = LabelKey.Log_In)
    private Panel centrePanel;
    private Label demoInfoLabel;
    private Label demoInfoLabel2;
    @Value(LabelKey.Authentication)
    private Label label;
    @Caption(caption = LabelKey.Password)
    private PasswordField passwordBox;
    private Label statusMsgLabel;
    @Caption(caption = LabelKey.Submit)
    private Button submitButton;
    @Caption(caption = LabelKey.User_Name, description = DescriptionKey.Enter_your_user_name)
    private TextField usernameBox;

    @Inject
    protected DefaultLoginView(LoginExceptionHandler loginExceptionHandler, SubjectProvider subjectProvider, Translate translate, @SessionBus
    PubSubSupport<BusMessage> eventBus) {
        super();
        this.loginExceptionHandler = loginExceptionHandler;
        this.subjectProvider = subjectProvider;
        this.translate = translate;
        this.eventBus = eventBus;
    }

    @Override
    public void buildView(ViewChangeBusMessage event) {
        super.buildView(event);
        getGridLayout().setColumns(3);
        getGridLayout().setRows(3);
        getGridLayout().setSizeFull();
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

        getGridLayout().addComponent(centrePanel, 1, 1);
        getGridLayout().setColumnExpandRatio(0, 1);
        getGridLayout().setColumnExpandRatio(2, 1);

        getGridLayout().setRowExpandRatio(0, 1);
        getGridLayout().setRowExpandRatio(2, 1);

    }

    @Override
    protected void setIds() {
        super.setIds();
        submitButton.setId(ID.getId(Optional.empty(), this, submitButton));
        usernameBox.setId(ID.getId(Optional.of("username"), this, usernameBox));
        passwordBox.setId(ID.getId(Optional.of("password"), this, passwordBox));
        statusMsgLabel.setId(ID.getId(Optional.of("status"), this, statusMsgLabel));
    }


    @Override
    public void buttonClick(ClickEvent event) {
        UsernamePasswordToken token = new UsernamePasswordToken(usernameBox.getValue(), passwordBox.getValue());
        try {
            subjectProvider.get()
                           .login(token);
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
            loginExceptionHandler.disabledAccount(this, token);
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
    public void setStatusMessage(String msg) {
        statusMsgLabel.setValue(msg);
    }

    @Override
    public void setStatusMessage(I18NKey messageKey) {
        setStatusMessage(translate.from(messageKey));
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
