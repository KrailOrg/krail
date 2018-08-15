package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'tr'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_tr extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "Tamam"},
                {ButtonType.ABORT.name(), "İptal"},
                {ButtonType.CANCEL.name(), "İptal"},
                {ButtonType.YES.name(), "Evet"},
                {ButtonType.NO.name(), "Hayır"},
                {ButtonType.CLOSE.name(), "Kapat"},
                {ButtonType.SAVE.name(), "Kaydet"},
                {ButtonType.RETRY.name(), "Yeniden Dene"},
                {ButtonType.IGNORE.name(), "Yoksay"},
                {ButtonType.HELP.name(), "Yardım"},
        };
    }

}

