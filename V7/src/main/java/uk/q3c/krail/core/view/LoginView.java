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
package uk.q3c.krail.core.view;

import com.vaadin.ui.Button;
import uk.q3c.krail.i18n.I18NKey;

/**
 * Bind this to your implementation in your ViewModule
 *
 * @author David Sowerby 1 Jan 2013
 */
public interface LoginView extends KrailView {

    void setUsername(String username);

    void setPassword(String password);

    Button getSubmitButton();

    /**
     * The message indicating login attempt pass or fail
     *
     * @return
     */
    String getStatusMessage();

    void setStatusMessage(I18NKey<?> messageKey);

    void setStatusMessage(String invalidLogin);

}
