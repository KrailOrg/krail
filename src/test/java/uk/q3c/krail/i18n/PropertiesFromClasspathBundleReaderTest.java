package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;

import java.io.IOException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
public class PropertiesFromClasspathBundleReaderTest {

    @Inject
    PropertiesFromClasspathBundleReader reader;

    @Test
    public void loadBundle() throws IOException {
        //given

        //when
        KrailResourceBundle bundle = reader.newBundle("properties", TestLabelKey.class, Locale.ITALIAN, this.getClass()
                                                                                               .getClassLoader(), true);
        //then
        assertThat(bundle).isNotNull();
        assertThat(bundle.getMap()
                         .keySet()).hasSize(1);
        assertThat(bundle.getValue(TestLabelKey.Yes)).isEqualTo("italian yes from properties");
    }
}