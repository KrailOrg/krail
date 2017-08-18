#Event Bus

#Introduction

Krail integrates the event bus provided by [MBassador](https://github.com/bennidi/mbassador).  For more information about the integration itself, see the this project's [contribution to the MBassador documentation](https://github.com/bennidi/mbassador/wiki/Guice-Integration).
 
There is no point duplicating MBassador's documentation here, but in brief, MBassador enables the use of synchronous or asynchronous event (message) buses.  MBassador is a sophisticated, high performance event bus, and it is strongly recommended that you read its documentation to get the best from it.<br><br>
There is a logical correlation between an event bus and a Guice scope, and that is what Krail provides - an event bus for Singleton, VaadinSession and UI scopes as described in the [Guice Scopes](tutorial-guice-scopes.md) chapter.  These can be accessed by:

- annotation (**@UiBus**, **@SessionBus**, **@GlobalBus**)
- provider (```UIBusProvider```, ```SessionBusProvider```, ```GlobalBusProvider```)

#The Tutorial task

We will create 3 buttons to publish messages, and receivers for events of each scope (UI, Session and Global).  By sending messages via the different buses we will be able to see how scope affects where the messages are received.  


#Create a page

If you have followed the Tutorial up to this point you will now be a complete expert in creating pages.  However, just in case you have stepped in to the Tutorial part way through (do developers really do that?), this is what you need to do:

- Amend the ```OtherPages``` module by adding the following line to the ```define()``` method:

```java
addEntry("events", EventsView.class, LabelKey.Events, PageAccessControl.PERMISSION);
```
- create the enum constant for the page
- create the view ```EventsView``` in *com.example.tutorial.pages* (code is provided later)
- create a package *com.example.tutorial.eventbus*
- in this new package, create a ```TutorialMessage``` class

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">An event (message) can be any object that implements the BusMessage interface</p>
</div>


- Our ```TutorialMessage``` will carry a String message and the sender.  Copy the following:

```
package com.example.tutorial.eventbus;

import uk.q3c.krail.eventbus.BusMessage;

public class TutorialMessage implements BusMessage {

    private final String msg;
    private Object sender;

    public TutorialMessage(String msg, Object sender) {
        this.msg = msg;
        this.sender = sender;
    }

    public String getMsg() {
        return msg + " from " + Integer.toHexString(sender.hashCode());
    }
}
```
#Message receivers

We will create a simple component to accept messages from a bus and display them in a ```TextArea```, and use this as a base class for each message receiver.

##Base class

- create a new class, ```MessageReceiver``` in *com.example.tutorial.eventbus*
- copy the code below


```
package com.example.tutorial.eventbus;


import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import net.engio.mbassy.listener.Handler;

public abstract class MessageReceiver extends Panel {
    private final TextArea textField;

    public MessageReceiver() {
        this.setSizeFull();
        this.textField = new TextArea();
        textField.setSizeFull();
        textField.setRows(8);
        setContent(textField);
    }

    public String getText() {
        return textField.getValue();
    }

    @Handler
    public void addMsg(TutorialMessage tutorialMessage) {
        String s = getText();
        textField.setValue(s+"\n"+tutorialMessage.getMsg());
    }
}
```

The **@Handler** annotation ensures the ```addMsg()``` method intercepts all ```TutorialMessage``` events which are passed by the bus(es) which the class is subscribed to.  We will subscribe in the following sub-classes, so that each one intercepts ```TutorialMessage``` events for a specific bus - but you can subscribe to multiple buses. 



##Receiver for each bus

- create three sub-classes, ```GlobalMessageReceiver```, ```SessionMessageReceiver``` and ```UIMessageReceiver``` each extending ```MessageReceiver```, in *com.example.tutorial.eventbus*

```
package com.example.tutorial.eventbus;

import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.eventbus.GlobalBus;
import uk.q3c.krail.eventbus.SubscribeTo;

@Listener @SubscribeTo(GlobalBus.class)
public class GlobalMessageReceiver extends MessageReceiver {
}
```

```
package com.example.tutorial.eventbus;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.eventbus.SubscribeTo;

@Listener @SubscribeTo(SessionBus.class)
public class SessionMessageReceiver extends MessageReceiver {

}

```

```
package com.example.tutorial.eventbus;

import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.core.eventbus.UIBus;

@Listener @SubscribeTo(UIBus.class)
public class UIMessageReceiver extends MessageReceiver {
}

```

The **@Listener** annotation marks the class as an ```MBassador``` bus subscriber.  The **@SubscribeTo** annotation is a Krail annotation to identify which bus or buses the class should be subscribed to.  The **@SubscribeTo** annotation is processed by Guice AOP, therefore the class must be instantiated by Guice for it to work. 

You could achieve the same by injecting a bus and directly subscribing:
   
```java
globalBusProvider.get().subscribe(this)
```
 

# Completing the View

- cut and paste the code below into ```EventsView``` 

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
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class EventsView extends Grid3x3ViewBase {
    private final UIBusProvider uiBusProvider;
    private final GlobalBusProvider globalBusProvider;
    @Caption(caption = LabelKey.Singleton, description = DescriptionKey.Singleton)
    private Button singletonSendBtn;
    @Caption(caption = LabelKey.Session, description = DescriptionKey.Session)
    private Button sessionSendBtn;
    @Caption(caption = LabelKey.UI, description = DescriptionKey.UI)
    private Button uiSendBtn;
    @Caption(caption = LabelKey.Refresh, description = DescriptionKey.Refresh)
    private Button refreshBtn;
    private SessionBusProvider sessionBusProvider;
    private GlobalMessageReceiver singletonMessageReceiver;
    private MessageReceiver sessionMessageReceiver;
    private MessageReceiver uiMessageReceiver;


    @Inject
    protected EventsView(Translate translate,UIBusProvider uiBusProvider, SessionBusProvider sessionBusProvider, GlobalBusProvider globalBusProvider,
                         GlobalMessageReceiver singletonMessageReceiver, SessionMessageReceiver sessionMessageReceiver, UIMessageReceiver uiMessageReceiver) {
        super(translate);
        this.uiBusProvider = uiBusProvider;
        this.sessionBusProvider = sessionBusProvider;
        this.singletonMessageReceiver = singletonMessageReceiver;
        this.sessionMessageReceiver = sessionMessageReceiver;
        this.uiMessageReceiver = uiMessageReceiver;
        this.globalBusProvider = globalBusProvider;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        constructEventSendButtons();
        layoutReceivers();
        refreshBtn = new Button();
        setTopRight(refreshBtn);
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
                             .publish(new TutorialMessage(m,this));
        });
        sessionSendBtn.addClickListener(click -> {
            String m = "Session";
            sessionBusProvider.get()
                              .publish(new TutorialMessage(m,this));
        });
        uiSendBtn.addClickListener(click -> {
            String m = "UI";
            uiBusProvider.get()
                         .publish(new TutorialMessage(m,this));
        });
        setTopLeft(singletonSendBtn);
        setMiddleLeft(sessionSendBtn);
        setBottomLeft(uiSendBtn);
    }
}

```

- create the enum constants

The ```constructEventSendButtons()``` method provides a button for each bus to send a message.

A bus for each scope is injected into the constructor using BusProviders

The Refresh button appears to do nothing, but that will become clear later. 

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">Buses can be injected using annotations (@GlobalBus etc) or providers (GlobalBusProvider etc).  Either will achieve the same result.  However, it is worth remembering that an annotated method parameter can be overridden in a sub-class using a different annotation</p>
</div>

A ```MessageReceiver``` is injected for each bus (remember these need to be instantiated by Guice) 
 

#Demonstrating the result

- run the application
- open a browser, which we will call browser 1 tab 1
- login as *'admin'*, *'password'*
- navigate to the *Event Bus* page
- open a second browser tab at the same URL - we will call this browser 1 tab 2 (now that surprised you!)
- in browser 1 tab 1 press each of the 3 buttons, Singleton, Session and UI
- Messages will appear in all 3 text areas
- Switch to tab 2 (there will be no messages visible yet)

If you know Vaadin, you will be familiar with this situation - the Vaadin client is unaware that changes have been made on the server, so the display has not been updated.  It will only update when the client is prompted to get an update from the server.  (We will come back to this when we address [Vaadin Push](tutorial-push.md)).  For our purposes, we just click the Refresh button.  This actually does nothing except cause the client to poll the server for updates.

- click Refresh
- the Singleton and Session text areas will contain a message from the same source, but the UI area will be empty

This demonstrates the scope of the event buses.  The UI bus is of UIScope - which means it relates to a browser tab (unless embedded).  The session scope relates to a browser instance, and therefore appears in both tabs, and a singleton scope applies to an application and also appears in both tabs.

- open a second browser instance (if you are using Chrome, be aware that Chrome does odd things with browser instances - to be certain you have a separate instance, it is better to use Firefox as the second instance)
- in browser 2, login as *'admin'*, *'password'*
- navigate to the *Event Bus* page
- switch back to browser 1 tab 1 and press each of the 3 buttons, Singleton, Session and UI again
- switch browser 2 tab 1
- press Refresh
- Only the Singleton text area will contain a message

This is what we expect - a Vaadin session relates to a browser instance, so a session message will not appear in browser 2 - only the Singleton will


#Summary

- We have covered the 3 defined event buses provided by Krail, with Singleton, Session and UI scope
- We have seen how to subscribe to a bus
- We have seen how to publish to a bus
- We have identified a challenge with refreshing the Vaadin client

#Download from GitHub
To get to this point straight from GitHub, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step10**

 

