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
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.TreeTable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.i18n.Caption;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.I18N;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.option.OptionPopup;
import uk.q3c.krail.core.option.VaadinOptionContext;
import uk.q3c.krail.core.option.VaadinOptionSource;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionKey;
import uk.q3c.krail.persist.PersistenceInfo;
import uk.q3c.krail.util.Experimental;

import java.lang.annotation.Annotation;

/**
 * A Panel containing information about a single data source
 * <p>
 * Created by David Sowerby on 30/06/15.
 */
@Experimental
@I18N
@Listener
@SubscribeTo(SessionBus.class)
public abstract class SourcePanel extends Panel implements VaadinOptionContext {


    public static final OptionKey<String> defaultCaptionStyleOptionKey = new OptionKey<>(ValoTheme.LABEL_SMALL, SourcePanel.class, LabelKey
            .Default_Caption_Style, DescriptionKey.Display_style_for_all_captions_unless_overridden);
    public static final OptionKey<String> defaultValueStyleOptionKey = new OptionKey<>("colored", SourcePanel.class, LabelKey.Default_Value_Style,
            DescriptionKey.Display_style_for_all_values_unless_overridden);


    public static final OptionKey<String> nameCaptionStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Name_Caption_Style, DescriptionKey
            .Display_style_for_the_name_caption);
    public static final OptionKey<String> nameValueStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Name_Style, DescriptionKey
            .Display_style_for_the_name);

    public static final OptionKey<String> descriptionCaptionStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Description_Caption_Style,
            DescriptionKey.Display_style_for_the_description_caption);
    public static final OptionKey<String> descriptionValueStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Description_Style, DescriptionKey
            .Display_style_for_the_description);

    public static final OptionKey<String> connectionUrlCaptionStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Connection_url_Caption_Style,
            DescriptionKey.Display_style_for_the_connection_url_caption);
    public static final OptionKey<String> connectionUrlValueStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Connection_url_Style,
            DescriptionKey.Display_style_for_the_connection_url);

    public static final OptionKey<String> is_volatileCaptionStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Is_volatile_Caption_Style,
            DescriptionKey.Display_style_for_the_is_volatile_caption);
    public static final OptionKey<String> is_volatileValueStyleOptionKey = new OptionKey<>("", SourcePanel.class, LabelKey.Is_volatile_Style, DescriptionKey
            .Display_style_for_the_is_volatile);


    protected final VaadinOptionSource optionSource;

    private final Label descriptionLabel;
    private final Label connectionUrlLabel;
    @Caption(caption = LabelKey.Source_Data, description = DescriptionKey.The_data_currently_held_in_this_source)
    private final TreeTable table;
    private final Label nameLabel;
    private final Label nameCaption;
    private final Label descriptionCaption;
    private final Label connectionUrlCaption;
    private final Label volatileCaption;
    private final Label volatileLabel;
    private final Button optionsButton;
    protected PersistenceInfo persistenceInfo;
    private Container container;
    private Option option;
    private OptionPopup optionPopup;
    private Translate translate;

    @Inject
    protected SourcePanel(Translate translate, VaadinOptionSource optionSource, Option option, OptionPopup optionPopup) {
        this.translate = translate;
        this.optionSource = optionSource;
        this.option = option;
        this.optionPopup = optionPopup;

        nameCaption = new Label();
        nameLabel = new Label();

        descriptionCaption = new Label();
        descriptionLabel = new Label();

        connectionUrlCaption = new Label();
        connectionUrlLabel = new Label();

        volatileCaption = new Label();
        volatileLabel = new Label();

        optionsButton = new Button();
        optionsButton.addClickListener(event -> optionPopup.popup(this, LabelKey.Options));

        table = new TreeTable();
        VerticalLayout layout = new VerticalLayout(nameCaption, nameLabel, descriptionCaption, descriptionLabel, connectionUrlCaption, connectionUrlLabel,
                volatileCaption, volatileLabel, optionsButton, table);
        this.setContent(layout);
        headings(null);
        styles();

    }

    private void styles() {
//        String defaultCaptionStyleName = option.get(defaultCaptionStyleOptionKey);
        applyStyle(nameCaption, nameCaptionStyleOptionKey);
        applyStyle(descriptionCaption, descriptionCaptionStyleOptionKey);
        applyStyle(connectionUrlCaption, connectionUrlCaptionStyleOptionKey);
        applyStyle(volatileCaption, is_volatileCaptionStyleOptionKey);

//        String defaultValueStyleName = option.get(defaultValueStyleOptionKey);
        applyStyle(nameLabel, nameValueStyleOptionKey);
        applyStyle(descriptionLabel, descriptionValueStyleOptionKey);
        applyStyle(connectionUrlLabel, connectionUrlValueStyleOptionKey);
        applyStyle(volatileLabel, is_volatileValueStyleOptionKey);
    }

    private void applyStyle(AbstractComponent component, OptionKey<String> key) {
        String componentStyleName = option.get(key);
        component.setStyleName(componentStyleName);
    }

    @Handler
    public final void headings(LocaleChangeBusMessage busMessage) {
        nameCaption.setValue(translate.from(LabelKey.Name));
        nameLabel.setDescription(translate.from(DescriptionKey.Name_of_the_source));
        descriptionCaption.setValue(translate.from(LabelKey.Description));
        descriptionLabel.setDescription(translate.from(DescriptionKey.Description_of_the_source));
        connectionUrlCaption.setValue(translate.from(LabelKey.Connection_URL));
        connectionUrlLabel.setDescription(translate.from(DescriptionKey.The_connection_string_for_this_source));
        volatileCaption.setValue(translate.from(LabelKey.Is_Volatile));
        volatileLabel.setDescription(translate.from(DescriptionKey.Data_is_held_in_memory));
        optionsButton.setCaption(translate.from(LabelKey.Options));
    }

    public Label getVolatileLabel() {
        return volatileLabel;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getConnectionUrlLabel() {
        return connectionUrlLabel;
    }

    public Label getNameCaption() {
        return nameCaption;
    }

    public Label getDescriptionCaption() {
        return descriptionCaption;
    }

    public Label getConnectionUrlCaption() {
        return connectionUrlCaption;
    }

    public Label getVolatileCaption() {
        return volatileCaption;
    }

    protected void displayInfo() {
        doSetPersistenceInfo();
        nameLabel.setValue(translate.from(persistenceInfo.getName()));
        descriptionLabel.setValue(translate.from(persistenceInfo.getDescription()));
        connectionUrlLabel.setValue(persistenceInfo.getConnectionUrl());

        I18NKey valueKey = persistenceInfo.isVolatilePersistence() ? LabelKey.Yes : LabelKey.No;
        volatileLabel.setValue(translate.from(valueKey));
        loadData();
    }

    protected void loadData() {
        container = optionSource.getContainer(getAnnotationClass());
        table.setContainerDataSource(container);
    }

    protected abstract Class<? extends Annotation> getAnnotationClass();


    protected abstract void doSetPersistenceInfo();

    public PersistenceInfo getPersistenceInfo() {
        return persistenceInfo;
    }

    public Label getDescriptionLabel() {
        return descriptionLabel;
    }

    /**
     * {@inheritDoc}
     *
     * @param event
     *         the event representing the change
     */

    @Override
    public void optionValueChanged(Property.ValueChangeEvent event) {
        styles();
        refreshData();
    }

    protected void refreshData() {
        loadData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option optionInstance() {
        return option;
    }
}
