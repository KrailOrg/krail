package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'ja'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_ja extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "OK"},
                {ButtonType.ABORT.name(), "中止"},
                {ButtonType.CANCEL.name(), "キャンセル"},
                {ButtonType.YES.name(), "はい"},
                {ButtonType.NO.name(), "いいえ"},
                {ButtonType.CLOSE.name(), "閉じる"},
                {ButtonType.SAVE.name(), "保存"},
                {ButtonType.RETRY.name(), "再試行"},
                {ButtonType.IGNORE.name(), "無視"},
                {ButtonType.HELP.toString(), "ヘルプ"},
        };
    }

}

