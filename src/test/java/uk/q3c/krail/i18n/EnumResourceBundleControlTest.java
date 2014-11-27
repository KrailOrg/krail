package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionModule;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, UserOptionModule.class})
public class EnumResourceBundleControlTest {

    Map<String, BundleReader> bundleReaders = new LinkedHashMap<>();
    Set<String> bundleSourceOrderDefault = new LinkedHashSet<>();
    Map<String, Set<String>> bundleSourceOrder = new HashMap<>();

    @Inject
    UserOption userOption;

    EnumResourceBundleControl bundleControl;


    @Test
    public void getFormats() {
        //given we just have the readers
        bundleReaders.put("class", new ClassBundleReader());
        bundleReaders.put("props", new PropertiesBundleReader());
        //when
        createControl(TestLabelKey.class);
        List<String> orderAny = bundleControl.getFormats("any");
        //then
        assertThat(orderAny).containsExactly("class", "props");

        //given we specify a default order in I18NModule
        bundleSourceOrderDefault.add("props");
        bundleSourceOrderDefault.add("class");
        createControl(TestLabelKey.class);
        //when
        orderAny = bundleControl.getFormats("any");
        //then
        assertThat(orderAny).containsExactly("props", "class");

        //given we change the order for just one baseName
        Set<String> tags = new LinkedHashSet<>();
        tags.add("class");
        tags.add("boots");
        bundleSourceOrder.put("TestLabels", tags);
        createControl(TestLabelKey.class);
        //when
        orderAny = bundleControl.getFormats("any");
        List<String> orderTestLabelKey = bundleControl.getFormats("TestLabels");
        //then
        assertThat(orderAny).containsExactly("props", "class");
        assertThat(orderTestLabelKey).containsExactly("class", "boots");

        //given user option changes the default
        //        bundleControl.setSourceOrderDefault("eat", "hat");
        //when
        orderAny = bundleControl.getFormats("any");
        orderTestLabelKey = bundleControl.getFormats("TestLabels");
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("eat", "hat");

        //given user option changes for a single baseName
        //        bundleControl.setSourceOrder("TestLabels", "fat", "cat");
        //when
        orderAny = bundleControl.getFormats("any");
        orderTestLabelKey = bundleControl.getFormats("TestLabels");
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("fat", "cat");

    }


    private void createControl(Class<? extends Enum> keyClass) {
        bundleControl = new EnumResourceBundleControl(bundleReaders);
        bundleControl.setEnumKeyClass(keyClass);
    }

    @Test
    public void getSourceOrder() {
        //given

        //when

        //then
        assertThat(true).isFalse();
    }

    @Test
    public void getSourceOrderDefault() {
        //given

        //when

        //then
        assertThat(true).isFalse();
    }
}