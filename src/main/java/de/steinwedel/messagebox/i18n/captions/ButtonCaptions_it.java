package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'it'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_it extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "OK"},
                {ButtonType.ABORT.name(), "Interrompi"},
                {ButtonType.CANCEL.name(), "Annulla"},
                {ButtonType.YES.name(), "Sì"},
                {ButtonType.NO.name(), "No"},
                {ButtonType.CLOSE.name(), "Chiudi"},
                {ButtonType.SAVE.name(), "Salva"},
                {ButtonType.RETRY.name(), "Riprova"},
                {ButtonType.IGNORE.name(), "Ignora"},
                {ButtonType.HELP.name(), "Aiuto"},
        };
    }

}

