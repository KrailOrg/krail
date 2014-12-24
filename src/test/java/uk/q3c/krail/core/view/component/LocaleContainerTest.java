/*
 * Copyright (C) 2014 David Sowerby
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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.testutil.TestUserOptionModule;
import uk.q3c.util.testutil.LogMonitor;
import uk.q3c.util.testutil.TestResource;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestUserOptionModule.class})
public class LocaleContainerTest {


    @Mock
    VaadinService vaadinService;

    @Inject
    UserOption userOption;

    @Inject
    LogMonitor logMonitor;

    LocaleContainer container;

    private Set<Locale> supportedLocales;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        userOption.configure(LocaleContainer.class, LocaleContainer.UserOptionProperty.class);
        File baseDir = TestResource.testJavaRootDir("krail");

        VaadinService.setCurrent(vaadinService);
        when(vaadinService.getBaseDirectory()).thenReturn(baseDir);
        supportedLocales = new HashSet<>();

    }

    @After
    public void teardown() {
        logMonitor.close();
    }

    @Test
    public void fillContainer_success() {
        // given
        supportedLocales.add(Locale.GERMANY);
        userOption.set(48, LocaleContainer.UserOptionProperty.LOCALE_FLAG_SIZE);
        // when
        container = new LocaleContainer(supportedLocales, userOption);
        // then
        assertThat(container.getItemIds()).hasSameSizeAs(supportedLocales);

        Item item = itemFor(Locale.GERMANY);
        assertThat(item).isNotNull();

        Property<?> property = item.getItemProperty(LocaleContainer.PropertyName.NAME);
        assertThat(property).isNotNull();
        assertThat(property.getValue()).isEqualTo(Locale.GERMANY.getDisplayName(Locale.GERMANY));

        property = item.getItemProperty(LocaleContainer.PropertyName.FLAG);
        assertThat(property).isNotNull();
        assertThat(property.getValue()).isInstanceOf(FileResource.class);
        FileResource flag = (FileResource) property.getValue();
        assertThat(flag.getFilename()).isEqualTo("de.png");
        assertThat(flag.getSourceFile()
                       .exists()).isEqualTo(true);

    }

    private Item itemFor(Locale locale) {
        Item item = container.getItem(locale.toLanguageTag());
        return item;
    }

    @Test
    public void fillContainer_no_flag_directory() {
        supportedLocales.add(Locale.GERMANY);
        userOption.set(47, LocaleContainer.UserOptionProperty.LOCALE_FLAG_SIZE);
        // when
        container = new LocaleContainer(supportedLocales, userOption);

        // then
        Item item = itemFor(Locale.GERMANY);
        assertThat(item).isNotNull();

        Property<?> property = item.getItemProperty(LocaleContainer.PropertyName.NAME);
        assertThat(property).isNotNull();
        assertThat(property.getValue()).isEqualTo(Locale.GERMANY.getDisplayName(Locale.GERMANY));

        property = item.getItemProperty(LocaleContainer.PropertyName.FLAG);
        assertThat(property.getValue()).isNull();
    }

    @Test
    public void fillContainer_missingFlag() {
        supportedLocales.add(Locale.CANADA);
        userOption.set(48, LocaleContainer.UserOptionProperty.LOCALE_FLAG_SIZE);
        // when
        container = new LocaleContainer(supportedLocales, userOption);

        // then
        Item item = itemFor(Locale.CANADA);
        assertThat(item).isNotNull();

        Property<?> property = item.getItemProperty(LocaleContainer.PropertyName.NAME);
        assertThat(property).isNotNull();
        assertThat(property.getValue()).isEqualTo(Locale.CANADA.getDisplayName());

        property = item.getItemProperty(LocaleContainer.PropertyName.FLAG);
        assertThat(property.getValue()).isNull();
    }



}
