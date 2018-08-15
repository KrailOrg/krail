package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'bs'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_bs extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "U redu"},
                {ButtonType.ABORT.name(), "Obustavi"},
                {ButtonType.CANCEL.name(), "Odustani"},
                {ButtonType.YES.name(), "Da"},
                {ButtonType.NO.name(), "Ne"},
                {ButtonType.CLOSE.name(), "Zatvori"},
                {ButtonType.SAVE.name(), "Snimi"},
                {ButtonType.RETRY.name(), "Pokušaj ponovo"},
                {ButtonType.IGNORE.name(), "Ignoriši"},
                {ButtonType.HELP.name(), "Pomoć"},
        };
    }

}

