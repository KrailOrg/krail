package uk.q3c.krail.core.view.component

import com.google.common.collect.ImmutableMap
import com.vaadin.server.FontAwesome
import com.vaadin.server.FontIcon
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.core.i18n.UserStatusKey
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable

/**
 *
 * Looks up an icon for a given [I18NKey]
 *
 * Created by David Sowerby on 29 Aug 2018
 */
interface IconFactory : Serializable {

    fun iconFor(key: I18NKey): FontIcon

}

class DefaultIconFactory : IconFactory {

    override fun iconFor(key: I18NKey): FontIcon {
        return map.getOrDefault(key, FontAwesome.QUESTION_CIRCLE)
    }

    private val map: ImmutableMap<I18NKey, FontIcon>

    init {
        val temp: MutableMap<I18NKey, FontIcon> = mutableMapOf()

        temp[CommonLabelKey.Cancel] = FontAwesome.TIMES_CIRCLE
        temp[CommonLabelKey.Edit] = FontAwesome.EDIT
        temp[CommonLabelKey.Save] = FontAwesome.SAVE
        temp[UserStatusKey.Log_In] = FontAwesome.SIGN_IN
        temp[UserStatusKey.Log_Out] = FontAwesome.SIGN_OUT
        temp[CommonLabelKey.Menu] = FontAwesome.BARS
        temp[CommonLabelKey.Home] = FontAwesome.HOME
        temp[CommonLabelKey.Help] = FontAwesome.QUESTION
        temp[CommonLabelKey.Settings] = FontAwesome.COG
        temp[CommonLabelKey.Notifications] = FontAwesome.BELL_SLASH

        map = ImmutableMap.copyOf(temp)
    }
}