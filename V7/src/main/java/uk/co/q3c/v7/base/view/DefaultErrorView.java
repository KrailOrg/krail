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


    public TextArea getTextArea() {
        return textArea;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public void setError(Throwable error) {
        this.error = error;
        textArea.setReadOnly(false);
        String s = StackTraceUtil.getStackTrace(error);
        textArea.setValue(s);
        textArea.setReadOnly(true);

    }

    /**
     * Called after the view itself has been constructed but before {@link #buildView()} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param event
     *         contains information about the change to this View
     */
    @Override
    public void beforeBuild(V7ViewChangeEvent event) {

    }

    @Override
    public void buildView(V7ViewChangeEvent event) {
        textArea = new TextArea();
        textArea.setSizeFull();
        viewBuilt = true;
        setRootComponent(textArea);
    }


}
