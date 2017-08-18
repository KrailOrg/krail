#Push

#Introduction

You may recall from the [Event Bus](tutorial-event-bus) chapter that a Vaadin client is unaware of changes made on the server.  We had to force the client to poll the server for updates by clicking a button.

To overcome this, Vaadin introduced 'Push' in version 7.1, a feature used to push messages from server to client.
 
Krail implements the process described in the [Vaadin Handbook](https://vaadin.com/book/-/page/advanced.push.html) and extends it slightly:

- a Broadcaster is implemented to enable any registered UI to push messages
- ScopedUI automatically registers with the Broadcaster, so that any UI can push a message
- ScopedUI listens for broadcast messages and distributes them via the UI Event Bus as instances of ```PushMessage```

#Fixing the Refresh Problem

##Modify the UI

- Add a **@Push** annotation to the ```TutorialUI```

```java
@Theme("valo")
@Push
public class TutorialUI extends DefaultApplicationUI {
```

##Broadcast a message

- remove the refresh button (we will no longer need that), and its **@Caption** 

When we press the Send Message buttons, we want to push a message as well.  In ```EventsView```:

- inject the ```Broadcaster``` and make it a field:
- modify each button click listener to broadcast (push) a message with a call to ```broadcaster.broadcast()```:

```
package com.example.tutorial.pages;

import com.example.tutorial.eventbus.*;
import com.example.tutorial.i18n.Caption;
import com.example.tutorial.i18n.DescriptionKey;
import com.example.tutorial.i18n.LabelKey;
import com.google.inject.Inject;
import com.vaadin.ui.Button;
import uk.q3c.krail.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.eventbus.SessionBusProvider;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.core.push.Broadcaster;
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class EventsView extends Grid3x3ViewBase {
    private final UIBusProvider uiBusProvider;
    private final GlobalBusProvider globalBusProvider;
    private Broadcaster broadcaster;
    @Caption(caption = LabelKey.Singleton, description = DescriptionKey.Singleton)
    private Button singletonSendBtn;
    @Caption(caption = LabelKey.Session, description = DescriptionKey.Session)
    private Button sessionSendBtn;
    @Caption(caption = LabelKey.UI, description = DescriptionKey.UI)
    private Button uiSendBtn;
    private SessionBusProvider sessionBusProvider;
    private GlobalMessageReceiver singletonMessageReceiver;
    private MessageReceiver sessionMessageReceiver;
    private MessageReceiver uiMessageReceiver;


    @Inject
    protected EventsView(Translate translate, UIBusProvider uiBusProvider, SessionBusProvider sessionBusProvider, GlobalBusProvider globalBusProvider,
                         GlobalMessageReceiver singletonMessageReceiver, SessionMessageReceiver sessionMessageReceiver, UIMessageReceiver uiMessageReceiver,
                         Broadcaster broadcaster) {
        super(translate);
        this.uiBusProvider = uiBusProvider;
        this.sessionBusProvider = sessionBusProvider;
        this.singletonMessageReceiver = singletonMessageReceiver;
        this.sessionMessageReceiver = sessionMessageReceiver;
        this.uiMessageReceiver = uiMessageReceiver;
        this.globalBusProvider = globalBusProvider;
        this.broadcaster = broadcaster;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        constructEventSendButtons();
        layoutReceivers();
    }

    private void layoutReceivers() {
        setTopCentre(singletonMessageReceiver);
        setMiddleCentre(sessionMessageReceiver);
        setBottomCentre(uiMessageReceiver);
    }

    private void constructEventSendButtons() {
        singletonSendBtn = new Button();
        sessionSendBtn = new Button();
        uiSendBtn = new Button();
        singletonSendBtn.addClickListener(click -> {
            String m = "Singleton";
            globalBusProvider.get()
                             .publish(new TutorialMessage(m, this));
            broadcaster.broadcast("refresh", m, this.getRootComponent());

        });
        sessionSendBtn.addClickListener(click -> {
            String m = "Session";
            sessionBusProvider.get()
                              .publish(new TutorialMessage(m, this));
            broadcaster.broadcast("refresh", m, getRootComponent());
        });
        uiSendBtn.addClickListener(click -> {
            String m = "UI";
            uiBusProvider.get()
                         .publish(new TutorialMessage(m, this));
            broadcaster.broadcast("refresh", m, getRootComponent());
        });
        setTopLeft(singletonSendBtn);
        setMiddleLeft(sessionSendBtn);
        setBottomLeft(uiSendBtn);
    }
}

```
##Verifying the change

We will now do the same sequence of tasks as for the [Event Bus](tutorial-event-bus), but without pressing the refresh button
   
  

- refresh Gradle
- run the application
- open a browser, which we will call browser 1 tab 1
- login as *'admin'*, *'password'*
- navigate to the *Event Bus* page
- open a second browser tab at the same URL - we will call this browser 1 tab 2
- in browser 1 tab 1 press each of the 3 buttons, Singleton, Session and UI
- Messages will appear in all 3 text areas
- Switch to tab 2 
- the Singleton and Session text areas will contain a message from the same source, but the UI area will be empty

This demonstrates the scope of the event buses.  The UI bus is of UIScope - which means it relates to a browser tab (unless embedded).  The session scope relates to a browser instance, and therefore appears in both tabs, and a singleton scope applies to an application and also appears in both tabs.

- open a second browser instance (if you are using Chrome, be aware that Chrome does odd things with browser instances - to be certain you have a separate instance, it is better to use Firefox as the second instance)
- in browser 2, login as *'admin'*, *'password'*
- navigate to the *Event Bus* page
- switch back to browser 1 tab 1 and press each of the 3 buttons, Singleton, Session and UI again
- switch browser 2 tab 1
- Only the Singleton text area will contain a message, as expected

#Using a Push Message

You may have noticed that we did not actually use the ```PushMessage```, just broadcasting it was enough to prompt the client to poll changes from the server.  We could, however, pick them up and use them as they are captured by the ```ScopedUI``` and despatched via the UI Bus.  To demonstrate this we will simply show the push messages in the UI state change log:

- Modify ```MessageReceiver``` by adding a getter
 
```
public TextArea getTextField() {
    return textField;
}
```

- Modify ```UIMessageReceiver``` to capture ```PushMessage``` instances and update the state change log:

```
package com.example.tutorial.eventbus;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.core.eventbus.UIBus;
import uk.q3c.krail.core.push.PushMessage;

@Listener
@SubscribeTo(UIBus.class)
public class UIMessageReceiver extends MessageReceiver {

    @Handler
    public void pushMessage(PushMessage pushMessage) {
        String s = getText();
        getTextField().setValue(s + "\n" + "Push message was originally from: "+pushMessage.getMessage());
    }
}
```

- run the application
- press any of the send message buttons, and an additional "push" message will appear in all the UI state log texts, of any UIs (browser tabs) you have open

#Footnote

Vaadin Push can be a little quirky.  This Tutorial was developed using Tomcat 8, and also checked on Tomcat 7 - but if you use something else and get problems, it is worth checking Vaadin's [notes on the subject](https://vaadin.com/wiki/-/wiki/Main/Working+around+push+issues) first.


#Summary

- We have broadcast a push message and seen that it causes the client to poll for updates, enabling immediate client refresh from a server based change.
- we have intercepted the push message after it has been re-distributed via the UI Bus


#Download from GitHub
To get to this point straight from GitHub, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step12**