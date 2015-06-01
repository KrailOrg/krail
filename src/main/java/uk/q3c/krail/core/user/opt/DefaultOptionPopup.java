/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.user.opt;

import com.google.inject.Inject;
import com.vaadin.ui.*;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.ui.DataTypeToUI;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.ID;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation for {@link OptionPopup}
 * <p>
 * Created by David Sowerby on 29/05/15.
 */
public class DefaultOptionPopup implements OptionPopup {
    private static Logger log = LoggerFactory.getLogger(DefaultOptionPopup.class);
    private Set<Field> contextFields;
    private DataTypeToUI dataTypeToUI;
    private Translate translate;

    @Inject
    public DefaultOptionPopup(DataTypeToUI dataTypeToUI, Translate translate) {
        this.dataTypeToUI = dataTypeToUI;
        this.translate = translate;
    }

    @Override
    public void popup(@Nonnull OptionContext context, I18NKey windowCaption) {
        Option option = context.getOption();
        Window window = new Window();
        window.setCaption(windowCaption(windowCaption));


        Map<OptionKey, Class<?>> keys = contextKeys(context);
        GridLayout baseLayout = new GridLayout(2, keys.size());
        baseLayout.setSizeUndefined();


        if (keys.size() == 0) {

            Label label = new Label(translate.from(LabelKey.No_Options_to_Show));
            baseLayout.addComponent(label, 0, 0);
        } else {
            calculateWindowSize(window, keys.size());
            int row = 0;
            for (OptionKey key : keys.keySet()) {
                Object value = option.get(key);
                AbstractField uiField = dataTypeToUI.componentFor(value);
                uiField.setCaption(translate.from(key.getKey()));
                uiField.setDescription(translate.from(key.getDescriptionKey()));
                uiField.setId(ID.getId(Optional.of(((Enum) key.getKey()).name()), this, uiField));
                //noinspection unchecked
                uiField.setValue(value);
                uiField.addValueChangeListener(event -> {
                    option.set(uiField.getValue(), key);
                    context.optionValueChanged(event);
                });

                Button defaultsButton = new Button(translate.from(LabelKey.Reset_to_Default));
                defaultsButton.setId(ID.getId(Optional.of(((Enum) key.getKey()).name()), this, defaultsButton));
                defaultsButton.addClickListener((event -> {
                    option.delete(0, key);
                    //we create an event to represent the field which whose value will be affected by this change
                    AbstractField.ValueChangeEvent changeEvent = new AbstractField.ValueChangeEvent(uiField);
                    context.optionValueChanged(changeEvent);
                    //update the value of the field - it may have changed
                    uiField.setValue(option.get(key));
                }));
                baseLayout.addComponent(new FormLayout(uiField), 0, row);
                baseLayout.addComponent(new FormLayout(defaultsButton), 1, row);
                row++;
            }
        }
        window.setId(ID.getId(Optional.empty(), context, this, window));
        window.setClosable(true);
        window.setContent(baseLayout);
        window.center();
        UI.getCurrent()
          .addWindow(window);
    }

    private void calculateWindowSize(Window window, int numOfKeys) {
        window.setSizeUndefined();
    }

    protected String windowCaption(I18NKey i18NKey) {
        return translate.from(i18NKey);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Map<OptionKey, Class<?>> contextKeys(OptionContext context) {
        captureContextFields(context);
        Map<OptionKey, Class<?>> keys = new HashMap<>();
        for (java.lang.reflect.Field field : contextFields) {
            field.setAccessible(true);

            try {
                OptionKey key = (OptionKey) field.get(context);
                if (key != null) {
                    keys.put(key, field.getType());
                }
            } catch (IllegalAccessException e) {
                log.error("Unable to access field {}", field.getName());
            }
        }
        return keys;
    }

    private void captureContextFields(OptionContext context) {
        //We should only do this once
        if (contextFields == null) {
            //noinspection unchecked
            contextFields = ReflectionUtils.getAllFields(context.getClass(), p -> p.getType()
                                                                                   .equals(OptionKey.class));
        }
    }


}
