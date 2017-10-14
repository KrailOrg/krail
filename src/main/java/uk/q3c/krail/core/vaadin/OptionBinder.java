package uk.q3c.krail.core.vaadin;

import com.vaadin.ui.AbstractField;
import uk.q3c.krail.option.OptionKey;

/**
 * Created by David Sowerby on 16 Oct 2017
 */
public interface OptionBinder {
    <P, M> void bindOption(OptionKey<M> optionKey, AbstractField<P> field);
}
