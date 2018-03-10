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
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.i18n.Caption;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Value;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.view.component.AssignComponentId;
import uk.q3c.krail.core.view.component.LoginFormException;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

public class DefaultLoginView extends Grid3x3ViewBase implements LoginView, ClickListener {
    private static Logger log = LoggerFactory.getLogger(DefaultLoginView.class);

    private final SubjectProvider subjectProvider;
    private final Translate translate;
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
    protected DefaultLoginView(SubjectProvider subjectProvider, Translate translate) {
        super(translate);
        this.subjectProvider = subjectProvider;
        this.translate = translate;
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
        subjectProvider.login(this, token);

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
