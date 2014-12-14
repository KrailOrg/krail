package uk.q3c.krail.i18n;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestUserOptionModule.class})
public class BundleReaderBaseTest {

    @Inject
    ClassBundleReader reader;

    @Test
    public void autoStub_null_value_autostub_off() {
        //given

        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }

    @Test
    public void autoStub_null_value_autostub_on_use_name() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, true, true, "na");
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("Yes");
    }

    @Test
    public void autoStub_null_value_autostub_on_not_use_name() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, true, false, "na");
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("na");
    }

    @Test(expected = NullPointerException.class)
    public void autoStub_null_value_autostub_on_not_use_name_null() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, true, false, null);
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("na");
    }


}