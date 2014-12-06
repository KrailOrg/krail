package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.TestUserOptionModule;
import uk.q3c.krail.core.user.opt.UserOption;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
public class EnumResourceBundleControlTest {

    Map<String, BundleReader> bundleReaders = new LinkedHashMap<>();
    Set<String> bundleSourceOrderDefault = new LinkedHashSet<>();
    Map<String, Set<String>> bundleSourceOrder = new HashMap<>();

    @Inject
    UserOption userOption;

    EnumResourceBundleControl bundleControl;


    @Test
    public void getFormats() {

        //given
        createControl(TestLabelKey.class);
        bundleControl.setSource("wiggly");

        //when
        List<String> result = bundleControl.getFormats(TestLabelKey.Blank.bundleName());

        //then
        assertThat(result).containsExactly("wiggly");

    }


    private void createControl(Class<? extends Enum> keyClass) {
        bundleControl = new EnumResourceBundleControl(bundleReaders);
        bundleControl.setEnumKeyClass(keyClass);
    }

}