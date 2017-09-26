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
package fixture.testviews2;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import uk.q3c.krail.core.navigate.DefaultNavigatorTest;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.I18NKey;

public class ViewB implements KrailView {


    private final Label label = new Label("not used");
    private DefaultNavigatorTest.TestViewChangeListener changeListener;


    @Inject
    public ViewB(DefaultNavigatorTest.TestViewChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void beforeBuild(ViewChangeBusMessage busMessage) {
        changeListener.addCall("beforeBuild", busMessage);
    }

    @Override
    public void buildView(ViewChangeBusMessage busMessage) {
        changeListener.addCall("buildView", busMessage);
    }

    @Override
    public Component getRootComponent() {
        return label;
    }



    @Override
    public void init() {
        changeListener.addCall("readFromEnvironment", null);
    }

    @Override
    public void afterBuild(AfterViewChangeBusMessage busMessage) {
        changeListener.addCall("afterBuild", busMessage);
    }


    @Override
    public I18NKey getNameKey() {
        return null;
    }

    @Override
    public void setNameKey(I18NKey nameKey) {

    }

    @Override
    public I18NKey getDescriptionKey() {
        return null;
    }

    @Override
    public void setDescriptionKey(I18NKey descriptionKey) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
