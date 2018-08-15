package de.steinwedel.messagebox.i18n.captions;

import de.steinwedel.messagebox.ButtonType;

import java.util.ListResourceBundle;

/**
 * I18n for the button captions. This class contains the translations for language code 'bg'.
 *
 * @author Dieter Steinwedel
 */
public class ButtonCaptions_bg extends ListResourceBundle {

    /**
     * See {@link ListResourceBundle#getContents()}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ButtonType.OK.name(), "ОК"},
                {ButtonType.ABORT.name(), "Прекъсване"},
                {ButtonType.CANCEL.name(), "Отказ"},
                {ButtonType.YES.name(), "Да"},
                {ButtonType.NO.name(), "Не"},
                {ButtonType.CLOSE.name(), "Затваряне"},
                {ButtonType.SAVE.name(), "Запазване"},
                {ButtonType.RETRY.name(), "Повторение"},
                {ButtonType.IGNORE.name(), "Пренебрегване"},
                {ButtonType.HELP.name(), "Помощ"},
        };
    }

}

