package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;
import uk.q3c.krail.core.user.opt.UserOption;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestUserOptionModule.class})
public class ClassBundleReaderTest {

    @Inject
    UserOption userOption;

    ClassBundleReader reader;

    @Test
    public void newBundle() {
        //given
        reader = new ClassBundleReader(userOption);
        //when
        EnumResourceBundle bundle = reader.newBundle("class", TestLabelKey.class, Locale.ITALIAN, this.getClass()
                                                                                                      .getClassLoader
                                                                                                              (),
                false);
        //then
        assertThat(bundle).isNotNull();
        assertThat(bundle.getValue(TestLabelKey.Yes)).isEqualTo("it_Yes");
    }
}