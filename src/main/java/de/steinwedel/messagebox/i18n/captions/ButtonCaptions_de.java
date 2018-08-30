package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'de'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_de extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "OK"},
                {ButtonType.ABORT.name(), "Abbrechen"},
                {ButtonType.CANCEL.name(), "Abbrechen"},
                {ButtonType.YES.name(), "Ja"},
                {ButtonType.NO.name(), "Nein"},
                {ButtonType.CLOSE.name(), "Schließen"},
                {ButtonType.SAVE.name(), "Speichern"},
                {ButtonType.RETRY.name(), "Erneut versuchen"},
                {ButtonType.IGNORE.name(), "Ignorieren"},
                {ButtonType.HELP.name(), "Hilfe"},
        };
    }

}

