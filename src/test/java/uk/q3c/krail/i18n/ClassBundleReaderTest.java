package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.testutil.TestUserOptionModule;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestUserOptionModule.class})
public class ClassBundleReaderTest {

    @Inject
    UserOption userOption;

    ClassBundleReader reader;

    @Test
    public void valueIsPresent() {
        //given

        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.ITALIAN);
        reader = new ClassBundleReader(userOption, new ClassBundleControl());
        //when
        Optional<String> value_it = reader.getValue(cacheKey, "class", false, false, "na");

        //then
        assertThat(value_it.isPresent()).isTrue();
        assertThat(value_it.get()).isEqualTo("it_Yes");
    }

    @Test
    public void valueFromBaseBundle() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Home, Locale.forLanguageTag(""));
        reader = new ClassBundleReader(userOption, new ClassBundleControl());
        //when
        Optional<String> value_en = reader.getValue(cacheKey, "class", false, false, "na");

        //then
        assertThat(value_en.isPresent()).isTrue();
        assertThat(value_en.get()).isEqualTo("home");
    }

    @Test
    public void valueNotPresent() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALIAN);
        reader = new ClassBundleReader(userOption, new ClassBundleControl());
        //when

        Optional<String> value = reader.getValue(cacheKey, "class", false, false, "na");
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
        reader = new ClassBundleReader(userOption, new ClassBundleControl());
        reader.getUserOption()
              .set(false, ClassBundleReader.UserOptionProperty.USE_KEY_PATH, "class");
        reader.getUserOption()
              .set("fixture1", ClassBundleReader.UserOptionProperty.PATH, "class");

        //when
        Optional<String> value = reader.getValue(cacheKey, "class", false, false, "na");
        //then
        assertThat(value.isPresent()).isTrue();
    }

    @Test
    public void bundle_does_not_exist() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALY);
        reader = new ClassBundleReader(userOption, new ClassBundleControl());
        //when
        Optional<String> value = reader.getValue(cacheKey, "class", false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }
}