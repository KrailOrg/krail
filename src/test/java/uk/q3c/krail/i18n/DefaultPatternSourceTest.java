package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.testutil.TestUserOptionModule;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
public class DefaultPatternSourceTest {


    @Inject
    UserOption userOption;

    DefaultPatternSource source;


    @Inject
    PatternCacheLoader patternCacheLoader;


    @Before
    public void setUp() throws Exception {
        //essential to stop pollution from one test to another
        ResourceBundle.clearCache();
        source = new DefaultPatternSource(userOption, patternCacheLoader);
    }

    /**
     * PatternSource is not required to check for a supportedLocale
     */
    @Test
    public void retrievePattern() {
        //given

        //when
        String value = source.retrievePattern(TestLabelKey.No, Locale.UK);
        //then
        assertThat(value).isEqualTo("No");

        //when supported locale
        value = source.retrievePattern(TestLabelKey.No, Locale.GERMANY);
        //then
        assertThat(value).isEqualTo("Nein");

        //when not a supported locale, it defaults to standard Java behaviour and uses default translation
        value = source.retrievePattern(TestLabelKey.No, Locale.CHINA);
        //then
        assertThat(value).isEqualTo("No");

        //when not a supported locale, but there is not even a default translation
        value = source.retrievePattern(TestLabelKey.ViewA, Locale.CHINA);
        //then
        assertThat(value).isEqualTo("ViewA");

        //when supported locale but no value for key
        value = source.retrievePattern(TestLabelKey.ViewA, Locale.UK);
        //then
        assertThat(value).isEqualTo("ViewA");
    }

    @Test
    public void clearCache() {
        //given
        String value = source.retrievePattern(TestLabelKey.No, Locale.UK);
        //when

        //then
        assertThat(source.getCache()
                         .size()).isEqualTo(1);

        //when
        source.clearCache();

        //then
        assertThat(source.getCache()
                         .size()).isEqualTo(0);
    }

    @Test
    public void clearCache_Source() {
        //given
        String value = source.retrievePattern(TestLabelKey.No, Locale.UK);
        value = source.retrievePattern(TestLabelKey.No, Locale.GERMANY);
        value = source.retrievePattern(TestLabelKey.Yes, Locale.ITALIAN);
        value = source.retrievePattern(TestLabelKey.Blank, new Locale(""));
        //when
        //Map map=source.getCache().asMap();
        //then
        assertThat(source.getCache()
                         .size()).isEqualTo(4);

        //when
        source.clearCache("class");

        //then
        assertThat(source.getCache()
                         .size()).isEqualTo(1);
    }
}


