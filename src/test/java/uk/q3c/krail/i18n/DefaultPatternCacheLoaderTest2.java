package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
public class DefaultPatternCacheLoaderTest2 {


    @Inject
    DefaultPatternCacheLoader loader;

    @Test
    public void setOptionStubWithKeyName() {
        //given

        //when
        loader.setOptionStubWithKeyName(true, "class");
        //then
        Boolean optionValue = loader.getUserOption()
                                    .get(false, DefaultPatternCacheLoader.UserOptionProperty.STUB_WITH_KEY_NAME,
                                            "class");
        assertThat(optionValue).isTrue();
    }

    @Test
    public void setOptionAutoStub() {
        //given

        //when
        loader.setOptionAutoStub(true, "class");
        //then
        Boolean optionValue = loader.getUserOption()
                                    .get(false, DefaultPatternCacheLoader.UserOptionProperty.AUTO_STUB, "class");
        assertThat(optionValue).isTrue();
    }

    @Test
    public void setOptionStubValue() {
        //given

        //when
        loader.setOptionStubValue("Wiggly", "class");
        //then
        String optionValue = loader.getUserOption()
                                   .get("bottoms", DefaultPatternCacheLoader.UserOptionProperty.STUB_VALUE, "class");
        assertThat(optionValue).isEqualTo("Wiggly");
    }
}