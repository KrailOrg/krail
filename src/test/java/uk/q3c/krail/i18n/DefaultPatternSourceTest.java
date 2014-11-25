package uk.q3c.krail.i18n;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;
import fixture.TestI18NModule;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionModule;
import uk.q3c.util.ResourceUtils;
import uk.q3c.util.testutil.TestResource;
import util.FileTestUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, UserOptionModule.class})
public class DefaultPatternSourceTest {

    @Inject
    @SupportedLocales
    Set<Locale> supportedLocales;

    @Inject
    UserOption userOption;

    @Mock
    VaadinService vaadinService;

    DefaultPatternSource source;

    @Inject
    ClassBundleWriter writer;

    @Inject
    Map<String, BundleReader> bundleReaders;


    @Before
    public void setUp() throws Exception {
        VaadinService.setCurrent(vaadinService);
        //essential to stop pollution from one test to another
        ResourceBundle.clearCache();
        source = new DefaultPatternSource(supportedLocales, userOption, bundleReaders);
    }

    /**
     * PatternSource is not required to check for a supportedLocale
     */
    @Test
    public void retrievePattern() {
        //given

        //when
        Optional<String> value = source.retrievePattern(TestLabelKey.No, Locale.UK);
        //then
        assertThat(value.get()).isEqualTo("No");

        //when supported locale
        value = source.retrievePattern(TestLabelKey.No, Locale.GERMANY);
        //then
        assertThat(value.get()).isEqualTo("Nein");

        //when not a supported locale, it defaults to standard Java behaviour and uses default translation
        value = source.retrievePattern(TestLabelKey.No, Locale.CHINA);
        //then
        assertThat(value.get()).isEqualTo("No");

        //when not a supported locale, but there is not even a default translation
        value = source.retrievePattern(TestLabelKey.ViewA, Locale.CHINA);
        //then
        assertThat(value).isEqualTo(Optional.absent());

        //when supported locale but no value for key
        value = source.retrievePattern(TestLabelKey.ViewA, Locale.UK);
        //then
        assertThat(value).isEqualTo(Optional.absent());
    }


    @Test
    public void generateStub_single_locale_Empty_value() {
        //given
        source.setGenerateStubWithName(false);
        //when
        source.generateStub(TestLabelKey.View1, Locale.GERMANY);
        Optional<String> value = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        //then
        assertThat(value.get()).isEqualTo("");
    }

    @Test
    public void generateStub_single_locale_value_is_key_name() {
        //given
        source.setGenerateStubWithName(true);
        //when
        source.generateStub(TestLabelKey.View1, Locale.GERMANY);
        Optional<String> value = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        //then
        assertThat(value.get()).isEqualTo("View1");
    }


    @Test
    public void generateStub_multiple_locales() {
        //given
        source.setGenerateStubWithName(false);
        //when
        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.GERMANY);
        locales.add(Locale.ITALY);
        source.generateStub(TestLabelKey.View1, locales);
        Optional<String> value1 = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        Optional<String> value2 = source.retrievePattern(TestLabelKey.View1, Locale.ITALY);
        //then
        assertThat(value1.get()).isEqualTo("");
        assertThat(value2.get()).isEqualTo("");
    }

    @Test
    public void generateStub_supported_locales() {
        //given
        source.setGenerateStubWithName(true);
        //when
        source.generateStub(TestLabelKey.View1);
        Optional<String> value1 = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        Optional<String> value2 = source.retrievePattern(TestLabelKey.View1, Locale.ITALY);
        Optional<String> value3 = source.retrievePattern(TestLabelKey.View1, Locale.UK);
        //then
        assertThat(value1.get()).isEqualTo("View1");
        assertThat(value2.get()).isEqualTo("View1");
        assertThat(value3.get()).isEqualTo("View1");
    }

    @Test
    public void writeOut_locales_provided_not_all_keys() throws IOException {
        //given
        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setWritePath(targetDir);

        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.GERMANY);
        locales.add(Locale.ITALY);
        locales.add(Locale.UK);
        File referenceFile = new File(TestResource.testResourceRootDir("krail"), "Labels.ref");
        File targetFile = new File(targetDir, "Labels.java");
        File referenceFile_de = new File(TestResource.testResourceRootDir("krail"), "Labels_de.ref");
        File targetFile_de = new File(targetDir, "Labels_de.java");
        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.ref");
        File targetFile_it = new File(targetDir, "Labels_it.java");

        //when
        source.writeOut(writer, LabelKey.class, locales, false);
        //then line 4 is the timestamp
        assertThat(FileTestUtil.compare(referenceFile, targetFile, 4)).isEqualTo(Optional.absent());
        assertThat(FileTestUtil.compare(referenceFile_de, targetFile_de, 4)).isEqualTo(Optional.absent());
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }


    @Test
    public void writeOut_locales_provided_all_keys_Name_value() throws IOException {
        //given
        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setWritePath(targetDir);

        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.ITALY);
        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.fullref");
        File targetFile_it = new File(targetDir, "Labels_it.java");

        //when
        source.writeOut(writer, LabelKey.class, locales, true);
        //then line 4 is the timestamp
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }

    @Test
    public void writeOut_locales_provided_all_keys_empty_value() throws IOException {
        //given

        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setWritePath(targetDir);
        source.setGenerateStubWithName(false);
        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.ITALY);
        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.fullrefEmpty");
        File targetFile_it = new File(targetDir, "Labels_it.java");

        //when
        source.writeOut(writer, LabelKey.class, locales, true);
        //then line 4 is the timestamp
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }


    @Test
    public void writeOut_supportedLocales() throws IOException {
        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            testOutDir.delete();
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setWritePath(targetDir);

        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.GERMANY);
        locales.add(Locale.ITALY);
        locales.add(Locale.UK);
        File referenceFile = new File(TestResource.testResourceRootDir("krail"), "Labels.ref");
        File targetFile = new File(targetDir, "Labels.java");
        File referenceFile_de = new File(TestResource.testResourceRootDir("krail"), "Labels_de.ref");
        File targetFile_de = new File(targetDir, "Labels_de.java");
        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.ref");
        File targetFile_it = new File(targetDir, "Labels_it.java");

        //when
        source.writeOut(writer, LabelKey.class, false);
        //then line 4 is the timestamp
        assertThat(FileTestUtil.compare(referenceFile, targetFile, 4)).isEqualTo(Optional.absent());
        assertThat(FileTestUtil.compare(referenceFile_de, targetFile_de, 4)).isEqualTo(Optional.absent());
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }


    @Test
    public void setKeyValueAndReset() {
        //given

        //when
        source.setKeyValue(TestLabelKey.Home, Locale.ITALY, "New Home");
        source.setKeyValue(TestLabelKey.Blank, Locale.ITALY, "New Blank");
        source.setKeyValue(TestLabelKey.Opt, Locale.ITALY, "New Opt");

        String pattern1 = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
                                .get();
        String pattern2 = source.retrievePattern(TestLabelKey.Blank, Locale.ITALY)
                                .get();
        String pattern3 = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
                                .get();
        //then new values set
        assertThat(pattern1).isEqualTo("New Home");
        assertThat(pattern2).isEqualTo("New Blank");
        assertThat(pattern3).isEqualTo("New Opt");

        //when reset
        source.reset(TestLabelKey.class);
        pattern1 = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
                         .get();
        pattern2 = source.retrievePattern(TestLabelKey.Blank, Locale.ITALY)
                         .get();
        pattern3 = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
                         .get();

        //then original values set
        assertThat(pattern1).isEqualTo("it_Home");
        assertThat(pattern2).isEqualTo("");
        assertThat(pattern3).isEqualTo("option");  // default because now there is no key in italian


    }

    @Test
    public void mergeSource_fromMap_OverwriteFalse() {
        //given
        EnumMap<TestLabelKey, String> map = new EnumMap<>(TestLabelKey.class);
        map.put(TestLabelKey.Home, "New Home");
        map.put(TestLabelKey.Blank, "New Blank");
        map.put(TestLabelKey.Opt, "New Opt");
        //when
        source.mergeSource(Locale.ITALY, map, false);
        //then does not overwrite existing
        String pattern = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
                               .get();
        assertThat(pattern).isEqualTo("it_Home");
        //then overwrites key with blank value
        pattern = source.retrievePattern(TestLabelKey.Blank, Locale.ITALY)
                        .get();
        assertThat(pattern).isEqualTo("New Blank");

        //then inserts where no key
        pattern = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
                        .get();
        assertThat(pattern).isEqualTo("New Opt");
    }

    @Test
    public void mergeSource_fromMap_OverwriteTrue() {
        //given
        EnumMap<TestLabelKey, String> map = new EnumMap<>(TestLabelKey.class);
        map.put(TestLabelKey.Home, "New Home");
        map.put(TestLabelKey.Opt, "New Opt");
        //when
        source.mergeSource(Locale.ITALY, map, true);
        //then overwrites existing
        String pattern = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
                               .get();
        assertThat(pattern).isEqualTo("New Home");
        //then overwrites blank
        pattern = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
                        .get();
        assertThat(pattern).isEqualTo("New Opt");
    }

    @Ignore("can only test when we have another type of source")
    @Test
    public void mergeSource_otherPatternSource_overwrite_true() {
        //given
        DefaultPatternSource otherSource = new DefaultPatternSource(supportedLocales, userOption, bundleReaders);
        otherSource.put(Locale.ITALY, TestLabelKey.Home, "New Home", true);
        otherSource.put(Locale.ITALY, TestLabelKey.Opt, "New Opt", true);
        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.ITALY);

        //when

        source.mergeSource(TestLabelKey.class, locales, otherSource, true);


        //then overwrites existing
        String pattern = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
                               .get();
        assertThat(pattern).isEqualTo("New Home");
        //then overwrites blank
        pattern = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
                        .get();
        assertThat(pattern).isEqualTo("New Opt");
    }

    @Ignore("can only test when we have another type of source")
    @Test
    public void mergeSource_otherPatternSource_overwrite_false() {
        //given
        DefaultPatternSource otherSource = new DefaultPatternSource(supportedLocales, userOption, bundleReaders);
        otherSource.put(Locale.ITALY, TestLabelKey.Home, "New Home", true);
        otherSource.put(Locale.ITALY, TestLabelKey.Opt, "New Opt", true);
        otherSource.put(Locale.ITALY, TestLabelKey.Blank, "New Blank", true);
        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.ITALY);
        //when
        source.mergeSource(TestLabelKey.class, locales, otherSource, false);

        //then does not overwrite existing
        String pattern = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
                               .get();
        assertThat(pattern).isEqualTo("it_Home");
        //then overwrites key with blank value
        pattern = source.retrievePattern(TestLabelKey.Blank, Locale.ITALY)
                        .get();
        assertThat(pattern).isEqualTo("New Blank");

        //then inserts where no key
        pattern = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
                        .get();
        assertThat(pattern).isEqualTo("New Opt");
    }

    @Test
    public void stubOnlyNotExisting() {
        //given

        //when
        source.generateStub(TestLabelKey.Home, Locale.ITALY);
        //then
        assertThat(source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
                         .get()).isEqualTo("it_Home");
        //when
        source.generateStub(TestLabelKey.Blank, Locale.ITALY);
        //then overwrites with name (that's the default for generate stub with name)
        assertThat(source.retrievePattern(TestLabelKey.Blank, Locale.ITALY)
                         .get()).isEqualTo("Blank");
    }

    @Test
    public void autoStub_on() {
        //given
        source.setAutoStub(true);
        //when

        //then
        assertThat(source.retrievePattern(TestLabelKey.My_Account, Locale.ITALY)
                         .get()).isEqualTo("My Account");
    }

    @Test
    public void autoStub_off() {
        //given

        //when

        //then
        assertThat(source.retrievePattern(TestLabelKey.My_Account, Locale.ITALY)).isEqualTo(Optional.absent());
    }
}


