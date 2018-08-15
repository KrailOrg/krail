package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'hu'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_hu extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "OK"},
                {ButtonType.ABORT.name(), "Megszakítás"},
                {ButtonType.CANCEL.name(), "Mégsem"},
                {ButtonType.YES.name(), "Igen"},
                {ButtonType.NO.name(), "nem"},
                {ButtonType.CLOSE.name(), "Bezárás"},
                {ButtonType.SAVE.name(), "Mentés"},
                {ButtonType.RETRY.name(), "Újra"},
                {ButtonType.IGNORE.name(), "Figyelmen kívül hagyás"},
                {ButtonType.HELP.name(), "Súgó"},
        };
    }

}

