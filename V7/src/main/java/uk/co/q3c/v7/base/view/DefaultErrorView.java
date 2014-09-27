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

package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.vaadin.ui.TextArea;
import uk.co.q3c.util.StackTraceUtil;
import uk.co.q3c.v7.base.navigate.NavigationState;

/**
 * @author David Sowerby 4 Aug 2013
 */

public class DefaultErrorView extends ViewBase implements ErrorView {

    private Throwable error;
    private TextArea textArea;
    private boolean viewBuilt = false;

    @Inject
    protected DefaultErrorView() {
        super();
    }

    @Override
    public void processParams(NavigationState navigationState) {

    }

    public TextArea getTextArea() {
        return textArea;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public void setError(Throwable error) {
        if (!viewBuilt) {
            buildView();
        }
        this.error = error;
        textArea.setReadOnly(false);
        String s = StackTraceUtil.getStackTrace(error);
        textArea.setValue(s);
        textArea.setReadOnly(true);

    }

    @Override
    protected TextArea buildView() {
        textArea = new TextArea();
        textArea.setSizeFull();
        viewBuilt = true;
        return textArea;
    }

    /**
     * Called immediately after construction of the view to enable setting up the view from URL parameters
     *
     * @param navigationState
     */
    @Override
    public void prepareView(NavigationState navigationState) {

    }
}
