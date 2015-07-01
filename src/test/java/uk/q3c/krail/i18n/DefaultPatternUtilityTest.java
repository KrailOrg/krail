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
import uk.q3c.krail.testutil.TestPersistenceModule;
import uk.q3c.util.ResourceUtils;
import uk.q3c.util.testutil.FileTestUtil;
import uk.q3c.util.testutil.TestResource;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestOptionModule.class, TestPersistenceModule.class, EventBusModule.class, UIScopeModule.class, VaadinSessionScopeModule
        .class})
public class DefaultPatternUtilityTest {


    @Inject
    PatternUtility utility;

    @Inject
    ClassBundleWriter writer;

    @Test
    public void writeOut_locales_provided_all_keys() throws IOException {
        //        given

        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setOptionWritePath(targetDir);

        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.GERMAN);
        locales.add(Locale.ITALIAN);
        locales.add(Locale.UK);
        locales.add(Locale.forLanguageTag(""));
        File referenceFile = new File(TestResource.testResourceRootDir("krail"), "Labels.ref");
        File targetFile = new File(targetDir, "Labels.java");
        File referenceFile_de = new File(TestResource.testResourceRootDir("krail"), "Labels_de.ref");
        File targetFile_de = new File(targetDir, "Labels_de.java");
        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.ref");
        File targetFile_it = new File(targetDir, "Labels_it.java");
        File referenceFile_en_GB = new File(TestResource.testResourceRootDir("krail"), "Labels_en_GB.ref");
        File targetFile_en_GB = new File(targetDir, "Labels_en_GB.java");
        // this is to make sure that setting the default does not mess things up
        Locale.setDefault(Locale.CANADA_FRENCH);

        //when
        utility.writeOut(writer, LabelKey.class, locales, Optional.empty());
        //then line 4 is the timestamp
        //first 15 lines covers class declaration and a specimen number of entries
        //  Test shold only break if an entry is added or deleted near the satart
        assertThat(FileTestUtil.compareFirst(10, referenceFile, targetFile, 4)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compareFirst(10, referenceFile_de, targetFile_de, 4)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compareFirst(10, referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compareFirst(10, referenceFile_en_GB, targetFile_en_GB, 4)).isEqualTo(Optional.empty());
    }


    @Test
    public void writeOutExclusive() throws IOException {
        //given
        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setOptionWritePath(targetDir);

        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.ref_exc");
        File targetFile_it = new File(targetDir, "Labels_it.java");
        //when
        utility.writeOutExclusive("class", writer, LabelKey.class, Locale.ITALIAN, Optional.empty());
        //then
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.empty());
    }

    @Test
    public void writeOutExclusive_renamed() throws IOException {
        //given
        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setOptionWritePath(targetDir);

        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "NewBundle_it.ref_exc");
        File targetFile_it = new File(targetDir, "NewBundle_it.java");
        //when
        utility.writeOutExclusive("class", writer, LabelKey.class, Locale.ITALIAN, Optional.of("NewBundle"));
        //then
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.empty());
    }
}