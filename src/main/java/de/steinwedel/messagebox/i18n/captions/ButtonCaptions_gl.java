package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'gl'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_gl extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "Aceptar"},
                {ButtonType.ABORT.name(), "Interromper"},
                {ButtonType.CANCEL.name(), "Cancelar"},
                {ButtonType.YES.name(), "Si"},
                {ButtonType.NO.name(), "Non"},
                {ButtonType.CLOSE.name(), "Pechar"},
                {ButtonType.SAVE.name(), "Gardar"},
                {ButtonType.RETRY.name(), "Tentar de novo"},
                {ButtonType.IGNORE.name(), "Ignorar"},
                {ButtonType.HELP.name(), "Axuda"},
        };
    }

}

