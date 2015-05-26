#UI & Themes
The [Vaadin handbook](https://vaadin.com/book/vaadin7/-/page/application.html) provides a full explanation of its architecture, and the role of the UI component.  
 
For the purposes of this Tutorial, it is enough to consider the ```UI``` to be a representation of a browser tab.  The ```DefaultApplicationUI``` is provided by Krail as a start point, but you may want to change elements of it or replace it completely.  The first Tutorial section gave an overview of the [DefaultApplicationUI](tutorial01.md#Exploring the Basic Application) if you need a refresher.

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">Krail currently has no method of setting the Theme or Server Push without sub-classing the UI, so you may need to do so just for that reason</p>
</div>

##Replacing a UI
To use your own UI:

- create a UI, in this example just sub-classing ```DefaultApplicationUI```.  As you can see, it uses a lot of injected objects - hopefully your IDE will create the constructor for you.
- don't forget the **@Inject** annotation for the constructor - it is very easy to miss when using IDE auto-completion
- just to make it do something visibly different we have disabled the breadcrumb and menu, with the calls to ```option.set()``` (Options will be covered in more detail [later](tutorial05.md))
```
package com.example.tutorial.app;

import com.google.inject.Inject;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.push.Broadcaster;
import uk.q3c.krail.core.push.PushMessageRouter;
import uk.q3c.krail.core.ui.ApplicationTitle;
import uk.q3c.krail.core.ui.DefaultApplicationUI;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.view.component.*;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.I18NProcessor;
import uk.q3c.krail.i18n.Translate;

public class TutorialUI extends DefaultApplicationUI {
    
    @Inject
    protected TutorialUI(Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory, ApplicationLogo logo, ApplicationHeader header, UserStatusPanel userStatusPanel, UserNavigationMenu menu, UserNavigationTree navTree, Breadcrumb breadcrumb, SubPagePanel subpage, MessageBar messageBar, Broadcaster broadcaster, PushMessageRouter pushMessageRouter, ApplicationTitle applicationTitle, Translate translate, CurrentLocale currentLocale, I18NProcessor translator, LocaleSelector localeSelector, UserNotifier userNotifier, Option option) {
        super(navigator, errorHandler, converterFactory, logo, header, userStatusPanel, menu, navTree, breadcrumb, subpage, messageBar, broadcaster,
                pushMessageRouter, applicationTitle, translate, currentLocale, translator, localeSelector, userNotifier, option);
        option.set(false, DefaultApplicationUI.optionBreadcrumbVisible);
        option.set(false, DefaultApplicationUI.optionMenuVisible);
    }
}
```
- Configure the ```BindingManager``` to use this new UI.  In this case we override a specific ```BindingManager``` method, because we need to override the default:
```
 @Override
    protected Module uiModule() {
        return new DefaultUIModule().uiClass(TutorialUI.class).applicationTitleKey(LabelKey.Krail_Tutorial);
    }
```
- Add the new key to ```LabelKey```, which your IDE will probably do for you.
- Run the application and confirm that the menu and breadcrumb have disappeared, and the application title has changed in the browser tab

#Themes
At the moment there is no alternative for setting the theme except by using the **@Theme** annotation provided by Vaadin. On the new ```TutorialUI```
- Set the theme with @Theme("valo")
```
@Theme("valo")
public class TutorialUI extends DefaultApplicationUI {

    @Inject
    protected TutorialUI(Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory, ApplicationLogo logo, ApplicationHeader header, UserStatusPanel userStatusPanel, UserNavigationMenu menu, UserNavigationTree navTree, Breadcrumb breadcrumb, SubPagePanel subpage, MessageBar messageBar, Broadcaster broadcaster, PushMessageRouter pushMessageRouter, ApplicationTitle applicationTitle, Translate translate, CurrentLocale currentLocale, I18NProcessor translator, LocaleSelector localeSelector, UserNotifier userNotifier, Option option) {
        super(navigator, errorHandler, converterFactory, logo, header, userStatusPanel, menu, navTree, breadcrumb, subpage, messageBar, broadcaster,
                pushMessageRouter, applicationTitle, translate, currentLocale, translator, localeSelector, userNotifier, option);
        option.set(false, DefaultApplicationUI.optionBreadcrumbVisible);
        option.set(false, DefaultApplicationUI.optionMenuVisible);
    }
}

```
- Run the application and observe the difference appearance.  

Valo is the most recent theme from Vaadin.  "Reindeer" is the default, which you have been using until now.  For more information about themes, see the [Vaadin Documentation](https://vaadin.com/book/-/page/themes.html).

#Summary
This was a short tutorial, covering the creation of a new UI and setting a Theme.


#Download from Github
To get to this point straight from Github, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step03**
