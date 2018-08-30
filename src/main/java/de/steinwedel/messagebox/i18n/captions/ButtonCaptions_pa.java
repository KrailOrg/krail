package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'pa'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_pa extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "ਠੀਕ ਹੈ"},
                {ButtonType.ABORT.name(), "ਛੱਡੋ"},
                {ButtonType.CANCEL.name(), "ਰੱਦ ਕਰੋ"},
                {ButtonType.YES.name(), "ਹਾਂ"},
                {ButtonType.NO.name(), "ਨਹੀਂ"},
                {ButtonType.CLOSE.name(), "ਬੰਦ ਕਰੋ"},
                {ButtonType.SAVE.name(), "ਸੰਭਾਲੋ"},
                {ButtonType.RETRY.name(), "ਮੁੜ-ਕੋਸ਼ਿਸ਼"},
                {ButtonType.IGNORE.name(), "ਅਣਡਿੱਠਾ"},
                {ButtonType.HELP.name(), "ਮੱਦਦ"},
        };
    }

}

