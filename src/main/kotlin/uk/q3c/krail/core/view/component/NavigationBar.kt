package uk.q3c.krail.core.view.component

import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.themes.ValoTheme
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate

/**
 * Created by David Sowerby on 28 Aug 2018
 *
 * A simple 'menu bar' - [build] must be called before using it
 *
 * Attempted to include a "FullScreen" option but that does not work
 */
class NavigationBar(val translate: Translate) : HorizontalLayout() {
    var titleKey: I18NKey = CommonLabelKey.Loading_
        set(value) {
            field = value
            titleLabel.value = translate.from(titleKey)
        }
    var buttonStyle: String = ValoTheme.BUTTON_ICON_ONLY
    var titleStyle: String = ValoTheme.LABEL_BOLD
    var helpPath: String = ""
    var showUserName = false
    private val titleLabel = Label()
    private lateinit var menuButton: Button
    private lateinit var homeButton: Button
    private lateinit var helpButton: Button
    private lateinit var notificationsButton: Button
    //    private lateinit var fullScreenButton: FullScreenButton
    private lateinit var statusComponents: UserStatusComponents
    private lateinit var settingsButton: Button

    fun build(navigator: Navigator, userStatusComponents: UserStatusComponents, iconFactory: IconFactory) {
        this.components.clear() // this method may be re-called when locale changes
        this.setWidth("100%")
        menuButton = addButton(translate, CommonLabelKey.Menu, iconFactory)
        homeButton = addButton(translate, CommonLabelKey.Home, iconFactory)
        homeButton.addClickListener { _ -> navigator.navigateTo(StandardPageKey.Public_Home) }

        if (helpPath.isNotEmpty()) {
            helpButton = addButton(translate, CommonLabelKey.Help, iconFactory)
            helpButton.addClickListener { _ -> navigator.navigateTo(helpPath) }
        } else {
            helpButton.isEnabled = false
        }


        notificationsButton = addButton(translate, CommonLabelKey.Notifications, iconFactory)
//        fullScreenButton = addFullScreenButton(translate, viewDisplayPanel)
        titleKey = CommonLabelKey.Loading_ // to trigger label value
        statusComponents = userStatusComponents
//        statusPanel.login_logout_Button.addStyleName(buttonStyle)
//        statusPanel.setSizeFull()
        settingsButton = addButton(translate, CommonLabelKey.Settings, iconFactory)
        statusComponents.login_logout_Button.addStyleName(buttonStyle)

//        addComponents(menuButton, homeButton, notificationsButton, fullScreenButton, titleLabel, statusComponents.usernameLabel, statusComponents.login_logout_Button, settingsButton)
        if (showUserName) {
            addComponents(menuButton, homeButton, notificationsButton, titleLabel, statusComponents.usernameLabel, statusComponents.login_logout_Button, settingsButton, helpButton)
        } else {
            addComponents(menuButton, homeButton, notificationsButton, titleLabel, statusComponents.login_logout_Button, settingsButton, helpButton)
        }




        this.setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER)
        this.setExpandRatio(titleLabel, 1f)
    }

//    private fun addFullScreenButton(translate: Translate): FullScreenButton {
////        val button = FullScreenButton(FontAwesome.EXPAND)
////        button.addStyleName(buttonStyle)
////        button.description = translate.from(CommonLabelKey.Full_Screen)
////        button.setFullScreenTarget(UI.getCurrent())
////        return button
//    }

    private fun addButton(translate: Translate, description: I18NKey, icon: IconFactory): Button {
        val button = Button(icon.iconFor(description))
        button.addStyleName(buttonStyle)
        button.description = translate.from(description)
        return button
    }


}
