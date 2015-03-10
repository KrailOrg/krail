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

package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.util.ResourceUtils;
import uk.q3c.util.testutil.FileTestUtil;
import uk.q3c.util.testutil.TestResource;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestOptionModule.class, EventBusModule.class, UIScopeModule.class, VaadinSessionScopeModule.class})
public class PropertiesBundleWriterTest {

    @Inject
    PropertiesBundleWriter writer;

    @Inject
    PatternUtility utility;


    @Test
    public void write() throws IOException {
        //given
        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setOptionWritePath(targetDir);
        Set<Locale> locales = new LinkedHashSet<>();
        locales.add((Locale.ITALIAN));
        locales.add((Locale.GERMAN));
        File referenceFile_de = new File(TestResource.testResourceRootDir("krail"), "TestLabels_de.properties_ref");
        File targetFile_de = new File(targetDir, "TestLabels_de.properties");
        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "TestLabels_it.properties_ref");
        File targetFile_it = new File(targetDir, "TestLabels_it.properties");
        //when
        utility.writeOut(writer, TestLabelKey.class, locales, Optional.empty());
        //then
        assertThat(FileTestUtil.compare(referenceFile_de, targetFile_de, 1)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 1)).isEqualTo(Optional.empty());
    }
}