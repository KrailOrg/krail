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

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.I18NKey;

public class ViewA11 implements KrailView {

    private Label label;

    @Override
    public void beforeBuild(ViewChangeBusMessage busMessage) {

    }

    @Override
    public void buildView(ViewChangeBusMessage busMessage) {
        label = new Label("Wiggly");
    }

    @Override
    public Component getRootComponent() {
        // return null;
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public void init() {
    }

    @Override
    public void afterBuild(AfterViewChangeBusMessage busMessage) {

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
