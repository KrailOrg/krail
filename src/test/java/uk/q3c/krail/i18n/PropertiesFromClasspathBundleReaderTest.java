package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;
import uk.q3c.krail.core.user.opt.UserOption;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
public class PropertiesFromClasspathBundleReaderTest {

    PropertiesFromClasspathBundleReader reader;


    @Inject
    UserOption userOption;

    @Before
    public void setup() {
        ResourceBundle.clearCache();
    }


    @Test
    public void valueIsPresent() {
        //given

        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.ITALIAN);
        reader = new PropertiesFromClasspathBundleReader(userOption, new PropertiesFromClasspathBundleControl());
        //when
        Optional<String> value_it = reader.getValue(cacheKey, "properties", false, false, "na");

        //then
        assertThat(value_it.isPresent()).isTrue();
        assertThat(value_it.get()).isEqualTo("italian yes from properties");
    }

    @Test
    public void valueFromBaseBundle() {

        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.forLanguageTag(""));
        reader = new PropertiesFromClasspathBundleReader(userOption, new PropertiesFromClasspathBundleControl());
        //when
        Optional<String> value_en = reader.getValue(cacheKey, "properties", false, false, "na");

        //then
        assertThat(value_en.isPresent()).isTrue();
        assertThat(value_en.get()).isEqualTo("yes from properties");
    }

    @Test
    public void valueNotPresent() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALIAN);
        reader = new PropertiesFromClasspathBundleReader(userOption, new PropertiesFromClasspathBundleControl());
        //when

        Optional<String> value = reader.getValue(cacheKey, "properties", false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }

    /**
     * Class containing key-value pairs is not in the same package as the key declaration
     */
    @Test
    public void alternativePath() throws ClassNotFoundException {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey2.Key1, Locale.forLanguageTag(""));
        reader = new PropertiesFromClasspathBundleReader(userOption, new PropertiesFromClasspathBundleControl());
        reader.getUserOption()
              .set(false, PropertiesFromClasspathBundleReader.UserOptionProperty.USE_KEY_PATH, "properties");
        reader.getUserOption()
              .set("fixture1", PropertiesFromClasspathBundleReader.UserOptionProperty.PATH, "properties");

        //when
        Optional<String> value = reader.getValue(cacheKey, "properties", false, false, "na");
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("key1 from properties");
    }

    @Test
    public void bundle_does_not_exist() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALY);
        reader = new PropertiesFromClasspathBundleReader(userOption, new PropertiesFromClasspathBundleControl());
        //when
        Optional<String> value = reader.getValue(cacheKey, "properties", false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }
}