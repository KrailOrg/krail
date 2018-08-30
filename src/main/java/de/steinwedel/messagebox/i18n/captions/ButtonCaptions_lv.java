package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'lv'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_lv extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "Labi"},
                {ButtonType.ABORT.name(), "Pārtraukt"},
                {ButtonType.CANCEL.name(), "Atcelt"},
                {ButtonType.YES.name(), "Jā"},
                {ButtonType.NO.name(), "Nē"},
                {ButtonType.CLOSE.name(), "Aizvērt"},
                {ButtonType.SAVE.name(), "Saglabāt"},
                {ButtonType.RETRY.name(), "Mēģināt vēlreiz"},
                {ButtonType.IGNORE.name(), "Ignorēt"},
                {ButtonType.HELP.name(), "Palīdzība"},
        };
    }

}

