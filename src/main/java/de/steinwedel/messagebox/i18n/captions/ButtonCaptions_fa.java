package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'fa'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_fa extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "تأیید"},
                {ButtonType.ABORT.name(), "ساقط کردن"},
                {ButtonType.CANCEL.name(), "لغو"},
                {ButtonType.YES.name(), "بله"},
                {ButtonType.NO.name(), "نه"},
                {ButtonType.CLOSE.name(), "بستن"},
                {ButtonType.SAVE.name(), "ذخیره"},
                {ButtonType.RETRY.name(), "تلاش دوباره"},
                {ButtonType.IGNORE.name(), "چشم‌پوشی"},
                {ButtonType.HELP.name(), "کمک"},
        };
    }

}

