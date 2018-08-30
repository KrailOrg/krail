package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'zh_CN'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_zh_CN extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "确定"},
                {ButtonType.ABORT.name(), "放弃"},
                {ButtonType.CANCEL.name(), "取消"},
                {ButtonType.YES.name(), "是"},
                {ButtonType.NO.name(), "否"},
                {ButtonType.CLOSE.name(), "关闭"},
                {ButtonType.SAVE.name(), "保存"},
                {ButtonType.RETRY.name(), "重试"},
                {ButtonType.IGNORE.name(), "忽略"},
                {ButtonType.HELP.name(), "帮助"},
        };
    }

}

