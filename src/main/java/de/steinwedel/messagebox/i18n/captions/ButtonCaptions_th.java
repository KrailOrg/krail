package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'th'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_th extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "ตกลง"},
                {ButtonType.ABORT.name(), "ยุติ"},
                {ButtonType.CANCEL.name(), "ยกเลิก"},
                {ButtonType.YES.name(), "ใช่"},
                {ButtonType.NO.name(), "ไม่"},
                {ButtonType.CLOSE.name(), "ปิด"},
                {ButtonType.SAVE.name(), "บันทึก"},
                {ButtonType.RETRY.name(), "ลองใหม่"},
                {ButtonType.IGNORE.name(), "ไม่สนใจ"},
                {ButtonType.HELP.name(), "ช่วยเหลือ"},
        };
    }

}

