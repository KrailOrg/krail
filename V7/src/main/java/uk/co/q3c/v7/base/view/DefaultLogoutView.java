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
import com.vaadin.ui.Panel;

import java.util.List;

public class DefaultLogoutView extends VerticalViewBase implements LogoutView {

    @Inject
    protected DefaultLogoutView() {
        super();
        buildView();
    }

    protected void buildView() {
        Panel p = new Panel("Logged out");
        p.setSizeFull();
        addComponent(p);
    }

    @Override
    public Component getRootComponent() {

        return this;

    }

    @Override
    protected void processParams(List<String> params) {

    }

    @Override
    public void enter(V7ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public String viewName() {

        return getClass().getSimpleName();
    }


}
