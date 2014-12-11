package uk.q3c.krail.i18n;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
public class DefaultPatternSourceTest {

    //    @Inject
    //    @BundleSourceOrderDefault
    //    Set<String> bundleSourceOrderDefault = new LinkedHashSet<>();
    //
    //    @Inject
    //    @BundleSourceOrder
    //    Map<String, Set<String>> bundleSourceOrder = new HashMap<>();
    //
    //    @Inject
    //    @SupportedLocales
    //    Set<Locale> supportedLocales;
    //
    //    @Inject
    //    UserOption userOption;
    //
    //    @Mock
    //    VaadinService vaadinService;
    //
    //    DefaultPatternSource source;
    //
    //    @Inject
    //    ClassBundleWriter writer;
    //
    //    @Inject
    //    Map<String, BundleReader> bundleReaders;
    //
    //    @Inject
    //    EnumResourceBundleControl bundleControl;


    @Before
    public void setUp() throws Exception {
        //        VaadinService.setCurrent(vaadinService);
        //        //essential to stop pollution from one test to another
        //        ResourceBundle.clearCache();
        //        source = new DefaultPatternSource(supportedLocales, userOption, bundleReaders, bundleControl,
        //                bundleSourceOrderDefault, bundleSourceOrder);
    }

    /**
     * PatternSource is not required to check for a supportedLocale
     */
    @Test
    public void retrievePattern() {
        //        //given
        //
        //        //when
        //        Optional<String> value = source.retrievePattern(TestLabelKey.No, Locale.UK);
        //        //then
        //        assertThat(value.get()).isEqualTo("No");
        //
        //        //when supported locale
        //        value = source.retrievePattern(TestLabelKey.No, Locale.GERMANY);
        //        //then
        //        assertThat(value.get()).isEqualTo("Nein");
        //
        //        //when not a supported locale, it defaults to standard Java behaviour and uses default translation
        //        value = source.retrievePattern(TestLabelKey.No, Locale.CHINA);
        //        //then
        //        assertThat(value.get()).isEqualTo("No");
        //
        //        //when not a supported locale, but there is not even a default translation
        //        value = source.retrievePattern(TestLabelKey.ViewA, Locale.CHINA);
        //        //then
        //        assertThat(value).isEqualTo(Optional.absent());
        //
        //        //when supported locale but no value for key
        //        value = source.retrievePattern(TestLabelKey.ViewA, Locale.UK);
        //        //then
        //        assertThat(value).isEqualTo(Optional.absent());
    }


    @Test
    public void generateStub_single_locale_Empty_value() {
        //        //given
        //        source.setGenerateStubWithName(false);
        //        //when
        //        source.generateStub("class", TestLabelKey.View1, Locale.GERMANY, true);
        //        Optional<String> value = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        //        //then
        //        assertThat(value.get()).isEqualTo("");
    }

    @Test
    public void generateStub_single_locale_value_is_key_name() {
        //        //given
        //        source.setGenerateStubWithName(true);
        //        //when
        //        source.generateStub("class", TestLabelKey.View1, Locale.GERMANY, true);
        //        Optional<String> value = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        //        //then
        //        assertThat(value.get()).isEqualTo("View1");
    }


    @Test
    public void generateStub_multiple_locales() {
        //        //given
        //        source.setGenerateStubWithName(false);
        //        //when
        //        Set<Locale> locales = new HashSet<>();
        //        locales.add(Locale.GERMANY);
        //        locales.add(Locale.ITALY);
        //        source.generateStub("class", TestLabelKey.View1, locales, true);
        //        Optional<String> value1 = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        //        Optional<String> value2 = source.retrievePattern(TestLabelKey.View1, Locale.ITALY);
        //        //then
        //        assertThat(value1.get()).isEqualTo("");
        //        assertThat(value2.get()).isEqualTo("");
    }

    @Test
    public void generateStub_supported_locales() {
        //given
        //        source.setGenerateStubWithName(true);
        //        //when
        //        source.generateStub("class", TestLabelKey.View1, true);
        //        Optional<String> value1 = source.retrievePattern(TestLabelKey.View1, Locale.GERMANY);
        //        Optional<String> value2 = source.retrievePattern(TestLabelKey.View1, Locale.ITALY);
        //        Optional<String> value3 = source.retrievePattern(TestLabelKey.View1, Locale.UK);
        //        //then
        //        assertThat(value1.get()).isEqualTo("View1");
        //        assertThat(value2.get()).isEqualTo("View1");
        //        assertThat(value3.get()).isEqualTo("View1");
    }

    @Test
    public void writeOut_locales_provided_not_all_keys() throws IOException {
        //given
        //        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        //        if (testOutDir.exists()) {
        //            FileUtils.deleteQuietly(testOutDir);
        //        }
        //        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        //        writer.setOptionWritePath(targetDir);
        //
        //        Set<Locale> locales = new HashSet<>();
        //        locales.add(Locale.GERMANY);
        //        locales.add(Locale.ITALY);
        //        locales.add(Locale.UK);
        //        File referenceFile = new File(TestResource.testResourceRootDir("krail"), "Labels.ref");
        //        File targetFile = new File(targetDir, "Labels.java");
        //        File referenceFile_de = new File(TestResource.testResourceRootDir("krail"), "Labels_de.ref");
        //        File targetFile_de = new File(targetDir, "Labels_de.java");
        //        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.ref");
        //        File targetFile_it = new File(targetDir, "Labels_it.java");
        //
        //        //when
        //        source.writeOut("class", writer, LabelKey.Yes, locales, false);
        //        //then line 4 is the timestamp
        //        assertThat(FileTestUtil.compare(referenceFile, targetFile, 4)).isEqualTo(Optional.absent());
        //        assertThat(FileTestUtil.compare(referenceFile_de, targetFile_de, 4)).isEqualTo(Optional.absent());
        //        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }


    @Test
    public void writeOut_locales_provided_all_keys_Name_value() throws IOException {
        //given
        //        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        //        if (testOutDir.exists()) {
        //            FileUtils.deleteQuietly(testOutDir);
        //        }
        //        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        //        writer.setOptionWritePath(targetDir);
        //
        //        Set<Locale> locales = new HashSet<>();
        //        locales.add(Locale.ITALY);
        //        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.fullref");
        //        File targetFile_it = new File(targetDir, "Labels_it.java");
        //
        //        //when
        //        source.writeOut("class", writer, LabelKey.Yes, locales, true);
        //        //then line 4 is the timestamp
        //        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }

    @Test
    public void writeOut_locales_provided_all_keys_empty_value() throws IOException {
        //given

        //        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        //        if (testOutDir.exists()) {
        //            FileUtils.deleteQuietly(testOutDir);
        //        }
        //        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        //        writer.setOptionWritePath(targetDir);
        //        source.setGenerateStubWithName(false);
        //        Set<Locale> locales = new HashSet<>();
        //        locales.add(Locale.ITALY);
        //        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.fullrefEmpty");
        //        File targetFile_it = new File(targetDir, "Labels_it.java");
        //
        //        //when
        //        source.writeOut("class", writer, LabelKey.Yes, locales, true);
        //        //then line 4 is the timestamp
        //        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }


    @Test
    public void writeOut_supportedLocales() throws IOException {
        //        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        //        if (testOutDir.exists()) {
        //            testOutDir.delete();
        //        }
        //        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        //        writer.setOptionWritePath(targetDir);
        //
        //        Set<Locale> locales = new HashSet<>();
        //        locales.add(Locale.GERMANY);
        //        locales.add(Locale.ITALY);
        //        locales.add(Locale.UK);
        //        File referenceFile = new File(TestResource.testResourceRootDir("krail"), "Labels.ref");
        //        File targetFile = new File(targetDir, "Labels.java");
        //        File referenceFile_de = new File(TestResource.testResourceRootDir("krail"), "Labels_de.ref");
        //        File targetFile_de = new File(targetDir, "Labels_de.java");
        //        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "Labels_it.ref");
        //        File targetFile_it = new File(targetDir, "Labels_it.java");
        //
        //        //when
        //        source.writeOut("class", writer, LabelKey.Yes, false);
        //        //then line 4 is the timestamp
        //        assertThat(FileTestUtil.compare(referenceFile, targetFile, 4)).isEqualTo(Optional.absent());
        //        assertThat(FileTestUtil.compare(referenceFile_de, targetFile_de, 4)).isEqualTo(Optional.absent());
        //        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 4)).isEqualTo(Optional.absent());
    }


    @Test
    public void setKeyValueAndReset() {
        //given

        //when
        //        source.setKeyValue("class", TestLabelKey.Home, Locale.ITALY, "New Home");
        //        source.setKeyValue("class", TestLabelKey.Blank, Locale.ITALY, "New Blank");
        //        source.setKeyValue("class", TestLabelKey.Opt, Locale.ITALY, "New Opt");
        //
        //        Optional<String> pattern1 = source.retrievePattern(TestLabelKey.Home, Locale.ITALY);
        //        Optional<String> pattern2 = source.retrievePattern(TestLabelKey.Blank, Locale.ITALY);
        //        Optional<String> pattern3 = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY);
        //        //then new values set
        //        assertThat(pattern1.get()).isEqualTo("New Home");
        //        assertThat(pattern2.get()).isEqualTo("New Blank");
        //        assertThat(pattern3.get()).isEqualTo("New Opt");
        //
        //        //when reset
        //        source.reset("class", TestLabelKey.Yes);
        //        pattern1 = source.retrievePattern(TestLabelKey.Home, Locale.ITALY);
        //        pattern2 = source.retrievePattern(TestLabelKey.Blank, Locale.ITALY);
        //        pattern3 = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY);
        //
        //        //then original values set
        //        assertThat(pattern1.get()).isEqualTo("it_Home");
        //        assertThat(pattern2.get()).isEqualTo("");
        //        assertThat(pattern3.get()).isEqualTo("option");  // default because now there is no key in italian


    }

    @Test
    public <E extends TestLabelKey> void mergeSource_fromMap_OverwriteFalse() {
        //given
        //        EnumMap<TestLabelKey, String> map = new EnumMap<>(TestLabelKey.class);
        //        map.put(TestLabelKey.Home, "New Home");
        //        map.put(TestLabelKey.Blank, "New Blank");
        //        map.put(TestLabelKey.Opt, "New Opt");
        //
        //
        //        EnumMap<TestLabelKey, String> map2 = source.getBundle("class", TestLabelKey.Yes, Locale.ITALY)
        //                                                   .getMap();
        //        //when
        //        source.mergeMaps(false, map, map2);
        //        //then does not overwrite existing
        //        String pattern = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
        //                               .get();
        //        assertThat(pattern).isEqualTo("it_Home");
        //        //then overwrites key with blank value
        //        pattern = source.retrievePattern(TestLabelKey.Blank, Locale.ITALY)
        //                        .get();
        //        assertThat(pattern).isEqualTo("New Blank");
        //
        //        //then inserts where no key
        //        pattern = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
        //                        .get();
        //        assertThat(pattern).isEqualTo("New Opt");
    }

    @Test
    public void mergeSource_fromMap_OverwriteTrue() {
        //given
        //        EnumMap<TestLabelKey, String> map = new EnumMap<>(TestLabelKey.class);
        //        map.put(TestLabelKey.Home, "New Home");
        //        map.put(TestLabelKey.Opt, "New Opt");
        //        EnumMap<TestLabelKey, String> map2 = source.getBundle("class", TestLabelKey.Yes, Locale.ITALY)
        //                                                   .getMap();
        //        //when
        //        source.mergeMaps(true, map, map2);
        //        //then overwrites existing
        //        String pattern = source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
        //                               .get();
        //        assertThat(pattern).isEqualTo("New Home");
        //        //then overwrites blank
        //        pattern = source.retrievePattern(TestLabelKey.Opt, Locale.ITALY)
        //                        .get();
        //        assertThat(pattern).isEqualTo("New Opt");
    }

    @Test
    public void mergeSource_sources_overwrite_true() {
        //given
        //        Set<Locale> locales = new HashSet<>();
        //        locales.add(Locale.UK);
        //        locales.add(Locale.ITALY);
        //
        //        //when
        //
        //        source.mergeSources(TestLabelKey.Yes, locales, true, "properties", "class");
        //        EnumResourceBundle<TestLabelKey> classBundleUK = source.getBundle("class", TestLabelKey.Yes, Locale
        // .UK);
        //        EnumResourceBundle<TestLabelKey> classBundleIT = source.getBundle("class", TestLabelKey.Yes, Locale
        // .ITALIAN);
        //
        //        //then overwrites existing
        //
        //        assertThat(classBundleUK.getValue(TestLabelKey.Yes)).isEqualTo("yes from properties");
        //        assertThat(classBundleIT.getValue(TestLabelKey.Yes)).isEqualTo("italian yes from properties");


    }

    @Test
    public void mergeSources_overwrite_false() {
        //given
        //given
        //        Set<Locale> locales = new HashSet<>();
        //        locales.add(Locale.UK);
        //        locales.add(Locale.ITALY);
        //
        //        //when
        //
        //        source.mergeSources(TestLabelKey.Yes, locales, false, "properties", "class");
        //        EnumResourceBundle<TestLabelKey> classBundleUK = source.getBundle("class", TestLabelKey.Yes, Locale.UK);
        //        EnumResourceBundle<TestLabelKey> classBundleIT = source.getBundle("class", TestLabelKey.Yes, Locale.ITALIAN);
        //
        //
        //        //then inserts because none exists
        //        assertThat(classBundleUK.getValue(TestLabelKey.Yes)).isEqualTo("yes from properties");
        //        //then does not overwrite, because a value already exists
        //        assertThat(classBundleIT.getValue(TestLabelKey.Yes)).isEqualTo("it_Yes");


    }

    @Test
    public void stubOnlyThoseNotExisting() {
        //given

        //when
        //        source.generateStub("class", TestLabelKey.Home, Locale.ITALY, false);
        //        //then
        //        assertThat(source.retrievePattern(TestLabelKey.Home, Locale.ITALY)
        //                         .get()).isEqualTo("it_Home");
        //        //when
        //        source.generateStub("class", TestLabelKey.Blank, Locale.ITALY, true);
        //        //then overwrites with name (that's the default for generate stub with name)
        //        assertThat(source.retrievePattern(TestLabelKey.Blank, Locale.ITALY)
        //                         .get()).isEqualTo("Blank");
    }

    @Test
    public void autoStub_on() {
        //given
        //        source.setAutoStub(true);
        //        //when
        //
        //        //then
        //        assertThat(source.retrievePattern(TestLabelKey.My_Account, Locale.ITALY)
        //                         .get()).isEqualTo("My Account");
    }

    @Test
    public void autoStub_off() {
        //given

        //when

        //then
        //        assertThat(source.retrievePattern(TestLabelKey.My_Account, Locale.ITALY)).isEqualTo(Optional.absent());
        assertThat((true)).isFalse();
    }

}


