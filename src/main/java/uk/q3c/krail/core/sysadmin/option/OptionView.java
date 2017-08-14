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

package uk.q3c.krail.core.sysadmin.option;

import com.google.inject.Inject;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import uk.q3c.krail.core.i18n.Caption;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.util.Experimental;

import static uk.q3c.krail.core.i18n.DescriptionKey.*;

/**
 * A view to manage {@link Option} sources and values
 * <p>
 * Created by David Sowerby on 30/06/15.
 */
@Experimental
public class OptionView extends ViewBase {

    @Caption(caption = LabelKey.Active_Source, description = The_Option_Source_currently_in_use)
    private final ActiveOptionSourcePanel activeOptionSourcePanel;
    @Caption(caption = LabelKey.Selected_Source, description = The_Option_Source_selected_for_display)
    private final SelectedOptionSourcePanel selectedOptionSourcePanel;
    @Caption(caption = LabelKey.Option_Source_Selection, description = Select_an_option_source_for_display)
    private final SelectionPanel selectionPanel;

    @Inject
    protected OptionView(ActiveOptionSourcePanel activeOptionSourcePanel, SelectedOptionSourcePanel selectedOptionSourcePanel, SelectionPanel selectionPanel,
                         Translate translate) {
        super(translate);
        this.activeOptionSourcePanel = activeOptionSourcePanel;
        this.selectedOptionSourcePanel = selectedOptionSourcePanel;
        this.selectionPanel = selectionPanel;
        nameKey = LabelKey.Options;
        descriptionKey = DescriptionKey.Options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        HorizontalLayout horizontalLayout = new HorizontalLayout(activeOptionSourcePanel, selectionPanel, selectedOptionSourcePanel);
        activeOptionSourcePanel.displayInfo();
        selectedOptionSourcePanel.displayInfo();
        setRootComponent(new Panel(horizontalLayout));

    }
}
