package uk.q3c.krail.core.view.component

import com.google.inject.Inject
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
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
 *
 * Note:  The field declarations for buttons etc could be avoided by just creating the components as part of the [addEntry] call.
 * However, that would mean that [FunctionalTestSupport] would not create fields for the page objects. (see https://github.com/KrailOrg/krail-testApp/issues/55)
 */
class NavigationBar @Inject constructor(val translate: Translate, val translatableComponents: TranslatableComponents, userStatusComponents: UserStatusComponents) : HorizontalLayout() {
    var titleKey: I18NKey = CommonLabelKey.Loading_
        set(value) {
            field = value
            titleLabel.valueKey = value
            titleLabel.value = translate.from(titleKey)
        }
    var buttonStyle: String = ValoTheme.BUTTON_ICON_ONLY
    var titleStyle: String = ValoTheme.LABEL_BOLD
    var helpPath: String = ""
    private val titleLabel = MutableI18NLabel(CommonLabelKey.Loading_)
    private var menuButton = Button()
    private var homeButton = Button()
    private var helpButton = Button()
    private var notificationsButton = Button()
    private var settingsButton = Button()
    private var login_logout_Button = Button()

    /**
     * login_logout_Button is managed by [userStatusComponents], and therefore not added to [translatableComponents]
     */
    init {
        login_logout_Button = userStatusComponents.login_logout_Button
        with(translatableComponents) {
            addEntry(component = menuButton, descriptionKey = CommonLabelKey.Menu)
            addEntry(component = homeButton, descriptionKey = CommonLabelKey.Home)
            addEntry(component = notificationsButton, descriptionKey = CommonLabelKey.Notifications)
            addEntry(component = titleLabel, descriptionKey = CommonLabelKey.Title, useIcon = false)
            addEntry(component = settingsButton, descriptionKey = CommonLabelKey.Settings)
            addEntry(component = helpButton, descriptionKey = CommonLabelKey.Help)
        }

    }

    fun build(navigator: Navigator) {
        this.setWidth("100%")
        homeButton.addClickListener { _ -> navigator.navigateTo(StandardPageKey.Public_Home) }
        titleLabel.addStyleName(titleStyle)

        if (helpPath.isNotEmpty()) {
            helpButton.addClickListener { _ -> navigator.navigateTo(helpPath) }
        } else {
            helpButton.isEnabled = false
        }

        login_logout_Button.addStyleName(buttonStyle)

        translatableComponents.components.keys.forEach { c ->
            if (c is Button) {
                c.addStyleName(buttonStyle)
            }
        }

        addComponents(menuButton, homeButton, notificationsButton, titleLabel, login_logout_Button, settingsButton, helpButton)

        this.setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER)
        this.setExpandRatio(titleLabel, 1f)
    }
}

