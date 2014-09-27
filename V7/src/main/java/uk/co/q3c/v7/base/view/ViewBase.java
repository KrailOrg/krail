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
package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.q3c.util.ID;

import java.util.List;

public abstract class ViewBase implements V7View {

    private static Logger log = LoggerFactory.getLogger(ViewBase.class);
    private Component rootComponent;

    @Inject
    protected ViewBase() {
        super();

    }

    /**
     * Calls {@link #setIds() after the View has been constructed}
     */
    public void init() {
        setIds();
    }

    /**
     * You only need to override / implement this method if you are using TestBench, or another testing tool which
     * looks
     * for debug
     * ids. If you do override it to add your own subclass ids, make sure you call super
     */
    protected void setIds() {
        getRootComponent().setId(ID.getId(this, getRootComponent()));
    }

    @Override
    public Component getRootComponent() {
        if (rootComponent == null) {
            rootComponent = buildView();
            setIds();
        }
        return rootComponent;
    }

    /**
     * Override this method to build the layout and components for this View
     *
     * @return
     */
    protected abstract Component buildView();

    @Override
    public void enter(V7ViewChangeEvent event) {
        log.debug("entered view: " + this.getClass()
                                         .getSimpleName() + " with uri " + event.getNavigationState());
        List<String> params = event.getNavigationState()
                                   .getParameterList();
        processParams(params);
    }

    /**
     * This method is called with the URI parameters separated from the "address" part of the URI, and is typically
     * used
     * to set up the state of a view in response to the parameter values
     *
     * @param params
     */
    protected abstract void processParams(List<String> params);

    @Override
    public String viewName() {
        return getClass().getSimpleName();
    }

}
