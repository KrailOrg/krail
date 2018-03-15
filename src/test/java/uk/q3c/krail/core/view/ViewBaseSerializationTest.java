package uk.q3c.krail.core.view;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import uk.q3c.krail.core.guice.BindingsCollator;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.text.MessageFormatMode;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.q3c.krail.core.guice.InjectorHolder.getInjector;
import static uk.q3c.krail.core.guice.InjectorHolder.setBindingsCollator;

/**
 * Created by David Sowerby on 14 Mar 2018
 */
public class ViewBaseSerializationTest {


    @Test
    public void serialisation() {
        setBindingsCollator(new TestBindingsCollator());

        TestView3 view = getInjector().getInstance(TestView3.class);
        byte[] output = SerializationUtils.serialize(view);

        TestView3 viewBack = (TestView3) SerializationUtils.deserialize(output);

//        viewBack.constructTransients();
        assertThat(viewBack.getNotSerializable()).isNotNull();
        assertThat(viewBack.getWi()).isNotNull();
        assertThat(viewBack.getWs()).isNotNull();
        assertThat(viewBack.getThingy1()).isNotNull();
        assertThat(viewBack.getThingy2()).isNotNull();
        assertThat(viewBack.getThingy1().getValue()).isEqualTo(1);
        assertThat(viewBack.getThingy2().getValue()).isEqualTo(2);
        assertThat(viewBack.getTranslate()).isNotNull();


    }

    @Test
    public void serialisingAClass() {
        Locale locale;
        setBindingsCollator(new TestBindingsCollator());
        TestView3 view = getInjector().getInstance(TestView3.class);
        Class<? extends Widget> wic = view.getWi().getClass();
        byte[] output = SerializationUtils.serialize(view.getWi().getClass());
        Class<?> back = (Class<?>) SerializationUtils.deserialize(output);
        assertThat(wic).isEqualTo(back);
        Key k = Key.get(back);
        Widget<String> widgetBack = (Widget<String>) getInjector().getInstance(k);
        System.out.println("done");
    }

    static class TestBindingsCollator implements BindingsCollator {

        @Override
        public List<Module> allModules() {
            ArrayList<Module> list = new ArrayList<>();
            list.add(testModule());
            return list;
        }

        public Module testModule() {
            return new AbstractModule() {
                @Override
                protected void configure() {
                    Thingy t1 = new Thingy();
                    t1.setValue(1);
                    Thingy t2 = new Thingy();
                    t2.setValue(2);
                    bind(Thingy.class).annotatedWith(Names.named("1")).toInstance(t1);
                    bind(Thingy.class).annotatedWith(Names.named("2")).toInstance(t2);
                    bind(Translate.class).to(SerializableTranslate.class);
                }
            };
        }
    }

    static class NotSerializable {
        String name = "wiggly";

        public NotSerializable() {
            System.out.println("constructing NotSerializable");
        }

    }

    static class SerializableTranslate implements Translate {

        @Override
        public String from(MessageFormatMode strictness, boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments) {
            return null;
        }

        @Override
        public String from(boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments) {
            return null;
        }

        @Override
        public String from(I18NKey key, Locale locale, Object... arguments) {
            return null;
        }

        @Override
        public String from(I18NKey key, Object... arguments) {
            return null;
        }

        @Override
        public Collator collator() {
            return null;
        }
    }

}
