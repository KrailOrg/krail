package uk.q3c.krail.core.vaadin;

import com.vaadin.data.ValueProvider;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionKey;

/**
 * Created by David Sowerby on 14 Oct 2017
 */
public class OptionValueProvider<SOURCE, TARGET> implements ValueProvider<SOURCE, TARGET> {


    private Option option;
    private OptionKey<TARGET> optionKey;

    public OptionValueProvider(Option option) {
        this.option = option;
    }

    @Override
    public TARGET apply(SOURCE source) {
        return option.get(optionKey);
    }

    public OptionKey<TARGET> getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(OptionKey<TARGET> optionKey) {
        this.optionKey = optionKey;
    }
}
