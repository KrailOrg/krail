package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;
import uk.q3c.util.ResourceUtils;
import uk.q3c.util.testutil.TestResource;
import util.FileTestUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
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
        assertThat(FileTestUtil.compare(referenceFile, targetFile, 4)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compare(referenceFile_de, targetFile_de, 4)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compare(referenceFile_en_GB, targetFile_en_GB, 4)).isEqualTo(Optional.empty());
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