package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'nl'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_nl extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "OK"},
                {ButtonType.ABORT.name(), "Afbreken"},
                {ButtonType.CANCEL.name(), "Annuleren"},
                {ButtonType.YES.name(), "Ja"},
                {ButtonType.NO.name(), "Nee"},
                {ButtonType.CLOSE.name(), "Sluiten"},
                {ButtonType.SAVE.name(), "Opslaan"},
                {ButtonType.RETRY.name(), "Opnieuw"},
                {ButtonType.IGNORE.name(), "Negeren"},
                {ButtonType.HELP.name(), "Help"},
        };
    }

}

