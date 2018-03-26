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
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.guice.SerializationSupport;

public abstract class VerticalViewBase extends ViewBase implements KrailView {
    private static Logger log = LoggerFactory.getLogger(VerticalViewBase.class);

    private VerticalLayout layout;

    @Inject
    protected VerticalViewBase(Translate translate, SerializationSupport serializationSupport) {
        super(translate, serializationSupport);
    }

    public VerticalLayout getLayout() {
        return layout;
    }

    /**
     * Override this method to build the layout and components for this View
     *
     * @param event
     *
     * @return
     */
    @Override
    public void buildView(ViewChangeBusMessage event) {
        layout = new VerticalLayout();
        setRootComponent(layout);
        super.buildView(event);

    }

    @Override
    public Component getRootComponent() {
        return layout;
    }


}
