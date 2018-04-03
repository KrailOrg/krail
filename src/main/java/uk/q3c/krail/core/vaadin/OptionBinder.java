package uk.q3c.krail.core.vaadin;

import com.vaadin.ui.AbstractField;
import uk.q3c.krail.option.OptionKey;

import java.io.Serializable;

/**
 * Created by David Sowerby on 16 Oct 2017
 */
public interface OptionBinder extends Serializable {
    <P, M> void bindOption(OptionKey<M> optionKey, AbstractField<P> field);
}
