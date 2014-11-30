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

/**
 * Created by David Sowerby on 28/11/14.
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, UserOptionModule.class})
public class DefaultPatternSourceTest2 {

    Map<String, BundleReader> bundleReaders = new LinkedHashMap<>();
    Set<String> bundleSourceOrderDefault = new LinkedHashSet<>();
    Map<String, Set<String>> bundleSourceOrder = new HashMap<>();

    @Inject
    @SupportedLocales
    Set<Locale> supportedLocales;

    @Inject
    UserOption userOption;

    @Inject
    EnumResourceBundleControl bundleControl;

    PatternSource patternSource;


    @Test
    public void bundleSourceOrder() {
        //given we just have the readers
        bundleReaders.put("class", new ClassBundleReader());
        bundleReaders.put("props", new PropertiesBundleReader());
        //when
        createPatternSource(TestLabelKey.class);
        List<String> orderAny = patternSource.bundleSourceOrder(LabelKey._nullkey_);
        //then
        assertThat(orderAny).containsExactly("class", "props");

        //given we specify a default order in I18NModule
        bundleSourceOrderDefault.add("props");
        bundleSourceOrderDefault.add("class");
        createPatternSource(TestLabelKey.class);
        //when
        orderAny = patternSource.bundleSourceOrder(LabelKey._nullkey_);
        //then
        assertThat(orderAny).containsExactly("props", "class");

        //given we change the order for just one bundleName
        Set<String> tags = new LinkedHashSet<>();
        tags.add("class");
        tags.add("boots");
        bundleSourceOrder.put("TestLabels", tags);
        createPatternSource(TestLabelKey.class);
        //when

        orderAny = patternSource.bundleSourceOrder(LabelKey._nullkey_);
        List<String> orderTestLabelKey = patternSource.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("props", "class");
        assertThat(orderTestLabelKey).containsExactly("class", "boots");

        //        given user option changes the default
        patternSource.setOptionSourceOrderDefault("eat", "hat");
        //when
        orderAny = patternSource.bundleSourceOrder(LabelKey._nullkey_);
        orderTestLabelKey = patternSource.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("eat", "hat");

        //given user option changes for a single bundleName
        patternSource.setOptionSourceOrder("TestLabels", "fat", "cat");
        //when
        orderAny = patternSource.bundleSourceOrder(LabelKey._nullkey_);
        orderTestLabelKey = patternSource.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("fat", "cat");

    }

    private void createPatternSource(Class<? extends Enum> keyClass) {
        patternSource = new DefaultPatternSource(supportedLocales, userOption, bundleReaders, bundleControl,
                bundleSourceOrderDefault, bundleSourceOrder);
    }
}