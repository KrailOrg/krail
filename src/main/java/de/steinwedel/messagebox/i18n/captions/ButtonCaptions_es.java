package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'es'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_es extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "Aceptar"},
                {ButtonType.ABORT.name(), "Interrumpir"},
                {ButtonType.CANCEL.name(), "Cancelar"},
                {ButtonType.YES.name(), "Sí"},
                {ButtonType.NO.name(), "No"},
                {ButtonType.CLOSE.name(), "Cerrar"},
                {ButtonType.SAVE.name(), "Guardar"},
                {ButtonType.RETRY.name(), "Reintentar"},
                {ButtonType.IGNORE.name(), "Ignorar"},
                {ButtonType.HELP.name(), "Ayuda"},
        };
    }

}

