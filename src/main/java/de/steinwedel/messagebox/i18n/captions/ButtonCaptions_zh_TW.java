package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'zh_TW'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_zh_TW extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "確定"},
                {ButtonType.ABORT.name(), "中止"},
                {ButtonType.CANCEL.name(), "取消"},
                {ButtonType.YES.name(), "是"},
                {ButtonType.NO.name(), "否"},
                {ButtonType.CLOSE.name(), "關閉"},
                {ButtonType.SAVE.name(), "儲存"},
                {ButtonType.RETRY.name(), "重試"},
                {ButtonType.IGNORE.name(), "忽略"},
                {ButtonType.HELP.name(), "說明"},
        };
    }

}

