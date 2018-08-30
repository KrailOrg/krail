package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'lt'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_lt extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "Gerai"},
                {ButtonType.ABORT.name(), "Nutraukti"},
                {ButtonType.CANCEL.name(), "Atsisakau"},
                {ButtonType.YES.name(), "Taip"},
                {ButtonType.NO.name(), "Ne"},
                {ButtonType.CLOSE.name(), "Užverti"},
                {ButtonType.SAVE.name(), "Įrašyti"},
                {ButtonType.RETRY.name(), "Bandyti vėl"},
                {ButtonType.IGNORE.name(), "Ignoruoti"},
                {ButtonType.HELP.name(), "Pagalba"},
        };
    }

}

