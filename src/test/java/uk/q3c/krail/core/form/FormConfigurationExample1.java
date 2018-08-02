package uk.q3c.krail.core.form;

import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import uk.q3c.krail.i18n.test.TestLabelKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Sowerby on 31 Jul 2018
 */
public class FormConfigurationExample1 extends FormConfiguration {


    /**
     * There is some apparently pointless duplication of calls - these are to ensure that all calls are fluent
     */
    @Override
    public void config() {
        type("o")
                .styleAttributes(new StyleAttributes().alignment(StyleAlignment.align_center))
                .type("other");

        section("a")
                .section("a-1")
                .layout(HorizontalLayout.class)
                .columnOrder("q", "a")
                .entityClass(Person.class)
                .fieldOrder("b", "c")
                .excludedProperties("x")
                .styleAttributes(new StyleAttributes().size(StyleSize.huge)).
                fieldOrder("b", "c", "d");

        List<KrailValidator<?>> validators = new ArrayList<>();
        validators.add(new MustBeFalse());

        section("a").property("p1")
                .caption(TestLabelKey.Login)
                .description(TestLabelKey.Opt)
                .converterClass(StringToIntegerConverter.class)
                .componentClass(InlineDateField.class)
                .validators(validators)
                .validator(new MustBeTrue())
                .propertyValueClass(int.class)
                .propertyValueClass(boolean.class);
    }

}
