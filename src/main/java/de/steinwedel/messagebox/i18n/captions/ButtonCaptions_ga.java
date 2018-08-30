package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'ga'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_ga extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "OK"},
                {ButtonType.ABORT.name(), "Tobscoir"},
                {ButtonType.CANCEL.name(), "Cealaigh"},
                {ButtonType.YES.name(), "Tá"},
                {ButtonType.NO.name(), "Níl"},
                {ButtonType.CLOSE.name(), "Dún"},
                {ButtonType.SAVE.name(), "Sábháil"},
                {ButtonType.RETRY.name(), "Atriail"},
                {ButtonType.IGNORE.name(), "Déan neamhaird de"},
                {ButtonType.HELP.name(), "Cabhair"},
        };
    }

}

