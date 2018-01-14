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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.mock.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.testutil.guice.vsscope.TestVaadinSessionScopeModule;
import uk.q3c.krail.util.ResourceUtils;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;
import uk.q3c.util.testutil.LogMonitor;
import uk.q3c.util.testutil.TestResource;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestOptionModule.class, InMemoryModule.class, UtilsModule.class, UtilModule.class, TestVaadinSessionScopeModule.class})
public class LocaleContainerTest {

    @Mock
    CurrentLocale currentLocale;

    @Mock
    VaadinService vaadinService;

    @Inject
    Option option;

    @Inject
    LogMonitor logMonitor;

    @Inject
    ResourceUtils resourceUtils;

    LocaleContainer container;


    private Set<Locale> supportedLocales;

    @Before
    public void setup() throws URISyntaxException {
        Locale.setDefault(Locale.UK);
        File sampleFile = TestResource.resource(this, "basedir-marker.txt");
        File baseDir = sampleFile.getParentFile();
        VaadinService.setCurrent(vaadinService);
        when(vaadinService.getBaseDirectory()).thenReturn(baseDir);
        supportedLocales = new HashSet<>();
        container = new LocaleContainer(supportedLocales, option, resourceUtils, currentLocale);
        when(currentLocale.getLocale()).thenReturn(Locale.UK);

    }

    @After
    public void teardown() {
        logMonitor.close();
    }

    @Test
    public void fillContainer_success() {
        // given
        supportedLocales.add(Locale.GERMANY);
        option.set(container.getOptionKeyFlagSize(), 48);
        // when
        container = new LocaleContainer(supportedLocales, option, resourceUtils, currentLocale);
        // then
        assertThat(container.getData()).hasSameSizeAs(supportedLocales);

        LocaleInfo item = itemFor(Locale.GERMANY);
        assertThat(item).isNotNull();

        assertThat(item.displayName()).isEqualTo(Locale.GERMANY.getDisplayName(Locale.GERMANY));

        assertThat(item.getFlag()).isInstanceOf(FileResource.class);
        FileResource flag = item.getFlag();
        assertThat(flag.getFilename()).isEqualTo("de.png");
        assertThat(flag.getSourceFile()
                .exists()).isEqualTo(true);

    }

    private LocaleInfo itemFor(Locale locale) {
        LocaleInfo item = null;
        for (LocaleInfo info : container.getData()) {
            if (locale.equals(info.getLocale())) {
                item = info;
            }
        }
        return item;
    }

    @Test
    public void fillContainer_no_flag_directory() {
        supportedLocales.add(Locale.GERMANY);
        option.set(container.getOptionKeyFlagSize(), 47);
        // when
        container = new LocaleContainer(supportedLocales, option, resourceUtils, currentLocale);

        // then
        LocaleInfo item = itemFor(Locale.GERMANY);
        assertThat(item).isNotNull();

        assertThat(item).isNotNull();
        assertThat(item.displayName()).isEqualTo(Locale.GERMANY.getDisplayName(Locale.GERMANY));

        assertThat(item.getFlag()).isNull();
    }

    @Test
    public void fillContainer_missingFlag() {
        supportedLocales.add(Locale.CANADA);
        option.set(container.getOptionKeyFlagSize(), 48);
        // when
        container = new LocaleContainer(supportedLocales, option, resourceUtils, currentLocale);

        // then
        LocaleInfo item = itemFor(Locale.CANADA);
        assertThat(item).isNotNull();


        assertThat(item.displayName()).isEqualTo(Locale.CANADA.getDisplayName());
        assertThat(item.getFlag()).isNull();
    }


}
