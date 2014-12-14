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
public class DefaultPatternCacheLoaderTest {

    Map<String, BundleReader> bundleReaders = new LinkedHashMap<>();
    Set<String> bundleSourceOrderDefault = new LinkedHashSet<>();
    Map<String, Set<String>> bundleSourceOrder = new HashMap<>();


    @Inject
    UserOption userOption;


    DefaultPatternCacheLoader loader;


    @Test
    public void bundleSourceOrder() {
        //given we just have the readers
        bundleReaders.put("class", new ClassBundleReader(userOption, new ClassBundleControl()));
        bundleReaders.put("props", new PropertiesFromClasspathBundleReader(userOption, new
                PropertiesFromClasspathBundleControl()));
        //when
        createLoader();
        List<String> orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        //then
        assertThat(orderAny).containsExactly("class", "props");

        //given we specify a default order in I18NModule
        bundleSourceOrderDefault.add("props");
        bundleSourceOrderDefault.add("class");

        //when
        createLoader();
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        //then
        assertThat(orderAny).containsExactly("props", "class");

        //given we change the order for just one bundleName
        Set<String> tags = new LinkedHashSet<>();
        tags.add("class");
        tags.add("boots");
        bundleSourceOrder.put(TestLabelKey.Blank.bundleName(), tags);

        //when
        createLoader();
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        List<String> orderTestLabelKey = loader.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("props", "class");
        assertThat(orderTestLabelKey).containsExactly("class", "boots");

        //        given user option changes the default

        loader.setOptionSourceOrderDefault("eat", "hat");
        //when
        createLoader();
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        orderTestLabelKey = loader.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("eat", "hat");

        //given user option changes for a single bundleName
        loader.setOptionSourceOrder(TestLabelKey.Blank.bundleName(), "fat", "cat");
        //when
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        orderTestLabelKey = loader.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("fat", "cat");


    }

    private void createLoader() {
        loader = new DefaultPatternCacheLoader(bundleReaders, userOption, bundleSourceOrder, bundleSourceOrderDefault);
    }
}