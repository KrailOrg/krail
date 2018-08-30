package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'cs'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_cs extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "OK"},
                {ButtonType.ABORT.name(), "Přerušit"},
                {ButtonType.CANCEL.name(), "Zrušit"},
                {ButtonType.YES.name(), "Ano"},
                {ButtonType.NO.name(), "Ne"},
                {ButtonType.CLOSE.name(), "Zavřít"},
                {ButtonType.SAVE.name(), "Uložit"},
                {ButtonType.RETRY.name(), "Zkusit znovu"},
                {ButtonType.IGNORE.name(), "Ignorovat"},
                {ButtonType.HELP.name(), "Nápověda"},
        };
    }

}

