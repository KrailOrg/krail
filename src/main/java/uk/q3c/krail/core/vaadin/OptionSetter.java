package uk.q3c.krail.core.vaadin;

import com.vaadin.server.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionKey;

/**
 * Created by David Sowerby on 14 Oct 2017
 */
public class OptionSetter<P, M> implements Setter<P, M> {
    private static Logger log = LoggerFactory.getLogger(OptionSetter.class);

    private OptionKey<M> optionKey;
    private Option option;


    public OptionSetter(OptionKey<M> optionKey, Option option) {
        this.optionKey = optionKey;
        this.option = option;
    }

    @Override
    public void accept(P p, M m) {
        log.info("option setter for " + optionKey.compositeKey() + " called with {} and {}", p, m);
        option.set(optionKey, m);
    }
}
