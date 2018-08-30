package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'ca'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_ca extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "D'acord"},
                {ButtonType.ABORT.name(), "Interromp"},
                {ButtonType.CANCEL.name(), "Cancel·la"},
                {ButtonType.YES.name(), "Sí"},
                {ButtonType.NO.name(), "No"},
                {ButtonType.CLOSE.name(), "Tanca"},
                {ButtonType.SAVE.name(), "Desa"},
                {ButtonType.RETRY.name(), "Reintenta"},
                {ButtonType.IGNORE.name(), "Ignora"},
                {ButtonType.HELP.name(), "Ajuda"},
        };
    }

}

