package uk.q3c.krail.i18n;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.UserOptionModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, UserOptionModule.class})
public class PropertiesBundleWriterTest {

    PropertiesBundleWriter writer;

    @Test
    public void write() {
        //given

        //when

        //then
        assertThat(true).isFalse();
    }
}