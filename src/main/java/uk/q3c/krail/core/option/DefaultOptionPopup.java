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

package uk.q3c.krail.core.option;

import com.google.inject.Inject;
import com.vaadin.data.Converter;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.ui.DataTypeToUI;
import uk.q3c.krail.core.vaadin.DefaultOptionBinder;
import uk.q3c.krail.core.vaadin.ID;
import uk.q3c.krail.core.vaadin.OptionBinder;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionContext;
import uk.q3c.krail.option.OptionKey;
import uk.q3c.krail.option.option.OptionKeyLocator;

import java.util.Map;
import java.util.Optional;

/**
 * Default implementation for {@link OptionPopup}
 * <p>
 * Created by David Sowerby on 29/05/15.
 */
@SuppressFBWarnings("URV_CHANGE_RETURN_TYPE") // not clear what this is trying to indicate
public class DefaultOptionPopup implements OptionPopup {
    private static Logger log = LoggerFactory.getLogger(DefaultOptionPopup.class);
    private OptionContext activeContext;
    private Map<OptionKey, Class<?>> contextKeys;
    private Translate translate;
    private OptionKeyLocator optionKeyLocator;
    private OptionBinder optionBinder;
    private DataTypeToUI dataTypeToUI;
    private Window window;

    @Inject
    public DefaultOptionPopup(Translate translate, OptionKeyLocator optionKeyLocator, OptionBinder optionBinder, DataTypeToUI dataTypeToUI) {
        this.translate = translate;
        this.optionKeyLocator = optionKeyLocator;
        this.optionBinder = optionBinder;
        this.dataTypeToUI = dataTypeToUI;
    }

    /**
     * The context is scanned for {@link OptionKey} instances.  If none are found a message is displayed saying there are no options.  A context is loaded
     * only once - the {@link OptionKey} instances are cached. The {@link DefaultOptionBinder} binds a UI Field with an {@link Option} value, combined with a {@link Converter} instance to enable conversion to and from the type needed for presentation (usually String).  <p>
     * Options are displayed in a grid of 2 columns, the first column containing a Vaadin component to display the option value and the second a button to
     * reset the value to default. The component and button are each wrapped in a FormLayout to position the caption to the left of the value<p>
     * A value change listener is attached to the Vaadin component to change the option value in response to the user changing the value ion the component.
     *
     * @param context       the context to take the options from
     * @param windowCaption the I18NKey to provide a window caption
     */
    @Override
    public void popup(OptionContext context, I18NKey windowCaption) {

        // changing context, so we need to re-read the context fields
        if (context != activeContext) {
            contextKeys = contextKeys(context);
        }

        Option option = context.optionInstance();
        if (window != null) {
            window.close();
        }
        window = new Window();

        window.setCaption(windowCaption(windowCaption));


        int rows = contextKeys.size() > 0 ? contextKeys.size() : 1;
        GridLayout baseLayout = new GridLayout(2, rows);
        baseLayout.setSizeUndefined();


        if (contextKeys.isEmpty()) {
            Label label = new Label(translate.from(LabelKey.No_Options_to_Show));
            baseLayout.addComponent(label, 0, 0);
        } else {
            calculateWindowSize(window);
            int row = 0;
            for (OptionKey<?> key : contextKeys.keySet()) {
                AbstractField<?> field = fieldForKey(key);
                optionBinder.bindOption(key, field);
                setFieldMetaData(key, field);
                log.debug("option field {} value is at {}", field.getCaption(), field.getValue());
                Button defaultsButton = new Button(translate.from(LabelKey.Reset_to_Default));
                Optional<String> optionKeyName = Optional.of(((Enum) key.getKey()).name());
                defaultsButton.setId(ID.getId(optionKeyName, this, defaultsButton));
                defaultsButton.addClickListener((event -> {
                    // reset to previous level by removing entry for user
                    option.delete(key, 0);
                    // reset the binding - the option value may be different
                    optionBinder.bindOption(key, field);
                }));
                baseLayout.addComponent(new FormLayout(field), 0, row);
                baseLayout.addComponent(new FormLayout(defaultsButton), 1, row);
                row++;
            }
        }
        window.setId(ID.getId(Optional.empty(), context, this, window));
        window.setClosable(true);
        //use panel to scroll
        window.setContent(new Panel(baseLayout));
        window.center();
        UI.getCurrent()
                .addWindow(window);
        this.activeContext = context;
    }

    private <M> AbstractField<M> fieldForKey(OptionKey<M> key) {
        return dataTypeToUI.componentFor(key.getDefaultValue());
    }


    private void setFieldMetaData(OptionKey<?> key, AbstractField<?> uiField) {
        uiField.setCaption(translate.from(key.getKey()));
        uiField.setDescription(translate.from(key.getDescriptionKey()));
        Optional<String> optionKeyName = Optional.of(((Enum) key.getKey()).name());
        uiField.setId(ID.getId(optionKeyName, this, uiField));
        log.debug("Component id for '{}' set to: '{}'", uiField.getCaption(), uiField.getId());
    }

    private void calculateWindowSize(Sizeable window) {
        window.setSizeUndefined();
        window.setHeight("600px");
    }

    protected String windowCaption(I18NKey i18NKey) {
        return translate.from(i18NKey);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public Map<OptionKey, Class<?>> contextKeys(OptionContext context) {
        return optionKeyLocator.contextKeyMap(context);
    }


    public Window getWindow() {
        return window;
    }
}
