#Introduction

We had a very [brief introduction](tutorial-pages-navigation.md#I18NIntro) to Krail's implementation of I18N in an earlier section of the Tutorial, and this gave us the ```LabelKey``` and ```DescriptionKey``` **enum** classes.

In this section we will cover most of the rest of Krail's I18N functionality. 

#Elements of I18N

A complete I18N "transaction" requires the following components:

- a key to identify a pattern
- Locale-specific patterns for as many languages you wish to support
- arguments to populate variables in the pattern (if there are any)
- a method for selecting the the correct language and applying the arguments to the pattern 
- a way of knowing which Locale to use

#Direct translation

Open up ```MyNews``` and you will recall that we have used ```String``` literals in a number of places.  This is going to make life difficult if ever we want to translate this application - and even if we just want to use the same phrase in a number of different places.
 
Let's replace the following literal with something more robust:

```java
popupButton = new Button("options");
```

- In ```doBuild()```, replace:
```java
popupButton = new Button("options");
```

with

```
popupButton = new Button(getTranslate().from(LabelKey.Options));

```
- create the 'Options' enum constant in ```LabelKey```

This simple step gives you:
 
1. refactoring support for the key
1. a default translation, which is the enum's ```name()``` method with underscores transposed to spaces.
1. a way to provide an alternative phrase, without changing the enum key (we will see that shortly)
1. an application which requires no code changes if additional language support is needed one day


<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">You may have noticed that we always use "natural case" for the I18N keys.  This is because it is very easy to get from natural to upper or lower case, and it greatly supports early development.  It is much harder to automatically convert from upper or lower case to natural case.<br><br>
If your coding standards require absolutely that enum constants must be upper case, you can either use that as your default or define a specific value for the key.</p>
</div>

##Message with Parameters

Now let us add a banner to the page, which will include some variable information.

- in ```doBuild()``` add:  
```
Label bannerLabel = new Label();
getGridLayout().addComponent(bannerLabel,0,0,1,0);
```




So far, all the I18 patterns have been simple - they have had no parameters.  Now we want a more complex message with some dynamic elements to it.

Krail core uses a convention which splits what could be a very long list of keys like this:

- Labels : short, usually one or two words, no parameters, generally used as captions
- Descriptions : longer, typically several words, no parameters, generally used in tooltips
- Messages : contains parameter(s). 


This is just a convention - which we will use in this Tutorial - but it is entirely your decision how you organise your keys.

###Creating a Key class

Assuming you have followed this Tutorial from the start, you have already seen how to [create a key class](tutorial-pages-navigation.md#I18NIntro).  We are going to add another now:

- in the 'com.example.tutorial.i18n' package, create an Enum class called 'MessageKey'.  It should implement the ```I18NKey``` interface
- create a MessageKey constant **Banner**

```
package com.example.tutorial.i18n;

import uk.q3c.krail.core.i18n.I18NKey;

public enum MessageKey implements I18NKey {
    Banner
}

```


This is going to be a long message, and because it has parameters, the default translation cannot be taken from the key name.  We will use a class based method for defining the pattern: 

- in the 'com.example.tutorial.i18n' package, create a class 'Messages'
- override the ```loadMap()``` method
```
package com.example.tutorial.i18n;

import uk.q3c.krail.core.persist.clazz.i18n.EnumResourceBundle;

public class Messages extends EnumResourceBundle<MessageKey> {
    
    @Override
    protected void loadMap() {
        
    }
}
```

Here you will see that we are extending ```EnumResourceBundle``` but for type safety, genericised with ```MessageKey```.  The ```loadMap()``` method enables entries to be put in a map. 

- now associate the **Banner** key with an I18N pattern - using a static import makes it more readable:

```
package com.example.tutorial.i18n;

import uk.q3c.krail.core.persist.clazz.i18n.EnumResourceBundle;

import static com.example.tutorial.i18n.MessageKey.*;

public class Messages extends EnumResourceBundle<MessageKey> {

    @Override
    protected void loadMap() {
        put(Banner,"The temperature today is {1}.  The CEO has noticed that her news channel {0}.");
    }
}

```

Each of the parameters - *{n}* - will take a value we supply as an argument.  The arguments:
 
1. must be supplied in the order of the numbers in the *{n}*, not the order in which they appear in the pattern (because different languages may require parameters in a different order).
1. must match the number of parameters.  If not, the whole translation is abandoned and the pattern string is returned unchanged. 

For detail, see the Krail ```MessageFormat``` class - and if you want to know why it is not the native Java ``MessageFormat`` class, see this [blog post](http://rndjava.blogspot.co.uk/2013/02/alternative-java-messageformat.html). 

Now let's display the banner

- set up a random temperature
- choose a key depending on whether the CEO News channel is selected
- add two keys to ```LabelKey```, **is_selected** and **is_not_selected**
- create a ```Label``` using the translated message with the two arguments (remember that 'temperature' is the second parameter, *{1}* in the pattern, even though it appears first).
```
    int temperature = (new Random().nextInt(40))-10;
    LabelKey selection = (option.get(ceoVisible)) ? LabelKey.is_selected : LabelKey.is_not_selected;
    
    Label bannerLabel = new Label(getTranslate().from(MessageKey.Banner,  selection, temperature));
    getGridLayout().addComponent(bannerLabel,0,0,2,0);
```

Parameters passed as ```I18NKey``` constants are also translated.  These are currently the only parameter types that are localised, see [open ticket](https://github.com/davidsowerby/krail/issues/428).

- Run the application, log in and and navigate to "MyNews" (login = 'eq', 'eq'), 
    - the banner has been expanded to include the variable values
- click on "options" and change the value for CEO New Channel - but the label does not change.
- To fix this
    - make ```bannerLabel``` a field
    - move the code to set the bannerLabel value to ```optionValueChanged```
    
    
The code for this in the ```doBuild()``` method is now:

```
    int temperature = (new Random().nextInt(40)) - 10;
    LabelKey selection = (option.get(ceoVisible)) ? LabelKey.is_selected : LabelKey.is_not_selected;

    bannerLabel = new Label();
    getGridLayout().addComponent(bannerLabel,0,0,2,0);
```
    
```optionValueChanged()``` is now:
    
```
@Override
public void optionValueChanged(Property.ValueChangeEvent event) {
    ceoNews.setVisible(option.get(ceoVisible));
    itemsForSale.setVisible(option.get(itemsForSaleVisible));
    vacancies.setVisible(option.get(vacanciesVisible));
    int temperature = (new Random().nextInt(40)) - 10;
    LabelKey selection = (option.get(ceoVisible)) ? LabelKey.is_selected : LabelKey.is_not_selected;
    bannerLabel.setValue(getTranslate().from(MessageKey.Banner, selection, temperature));
}
```

- Rerun the application, login and select 'My News' page, and try changing the option to display the CEO new channel


#Translation from Annotations

When using Vaadin components, it is often more convenient to use an ```Annotation``` instead of calling ```Translate``` directly - this keeps the ```I18NKey```s with the fields using them.

To achieve this, we need an annotation that is specific to our ```I18NKey``` implementations (we cannot use annotations from Krail core, because of the limitations Java places on ```Annotation``` parameters)

- in the package 'com.example.tutorial.i18n', create a new Annotation class called "Caption".  Note the ```@I18NAnnotation``` - this tells Krail's ```I18NAnnotationProcessor``` that this annotation is used for I18N. 

```java
package com.example.tutorial.i18n;

import uk.q3c.krail.core.i18n.I18NAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@I18NAnnotation
public @interface Caption {

    LabelKey caption();

    DescriptionKey description();

}
```

The annotation itself can be called anything, but it must be annotated ```@I18NAnnotation```, and its methods be one or more of:

1. ```I18NKey implementation``` caption() - *used for component captions*
1. ```I18NKey implementation``` description() - *used for component tooltips*
1. ```I18NKey implementation``` value() - *used where a component implements the Property interface, typically Label*
1. ```String locale()``` - *a locale String to force use of a specific locale for this annotation* (see ```Locale.toLanguageTag()``` for the String format)

You may combine these methods in any way you wish - Krail's I18N annotation scanner (```I18NProcessor```) just looks for annotations which are annotated with ```@I18NAnnotation``` and for any methods in them which match those listed above.

- remove the translate method from the construction of ```popupButton``` in ```doBuild()```
```
popupButton = new Button();
```
- replace it by annotating the ```popupButton``` field
```
@Caption(caption = LabelKey.Options,description = DescriptionKey.Select_your_options)
private Button popupButton;
```
- create the constant for ```DescriptionKey```

- Run the application, log in and and navigate to "MyNews" (login = 'eq', 'eq')
    - The "Options" button will be the same as before, but of course the caption is generated by the annotation
    - The tooltip for the "Options" button will now say "Select your options"
    
    
##Limitation

Naturally, you cannot use variable values with an annotation - by its very nature, ```Annotation``` will only take static values. For I18N patterns which requires dynamic values, therefore, you will need to use a direct call to ```Translate```.


#Multi-Language

Even though Krail's approach to handling I18N is actually very useful even in a single language application, the whole point of I18N is, of course, to support multiple languages / Locales.  

By default, ```I18NModule``` defaults everything to **Locale.UK**.  This section assumes that you are familiar with the standard Java approach to I18N.  For those not familiar with it, there are many online resources if you need them.
<a name="config-methods"></a>
##Methods of configuration

Krail uses the ```I18NModule``` to configure how I18N operates.  There are two fundamental ways to define that configuration (as with most modules):
 
1. Use fluent methods provided by the module, to use at the point of construction in the ```BindingManager```.
1. Sub-class ```I18NModule```and use the sub-class in the ```BindingManager```

It really does not matter which method you use.  We will use method 2 for this example, but then show how method 1 would achieve the same result, but not actually apply it.

- in the package 'com.example.tutorial.i18n', create a new class 'TutorialI18NModule' extending from ```I18NModule```
- override the ```define()``` method
```java
package com.example.tutorial.i18n;

import uk.q3c.krail.core.i18n.I18NModule;

public class TutorialI18NModule extends I18NModule {

    @Override
    protected void define() {
    }
}
```
In this method we will define everything we need to.

- set the default locale explicitly, and add another Locale that we want to support. (The default locale is automatically a supported locale)

```
@Override
    protected void define() {
        defaultLocale(Locale.UK);
        supportedLocales(Locale.GERMANY);
    }
}
```
- call the new class in ```BindingManager```
```
@Override
protected Module i18NModule() {
    return new TutorialI18NModule();
}
```


- in the package 'com.example.tutorial.i18n', create an new class 'Messages_de' extended from ```Messages```

```
package com.example.tutorial.i18n;

import static com.example.tutorial.i18n.MessageKey.Banner;

public class Messages_de extends Messages {
    @Override
    protected void loadMap() {
        put(Banner, "Die Temperatur ist heute {1}. Der CEO hat bemerkt, dass ihre Nachrichten-Kanal {0}");
    }
}

```
To translate the keys used for parameter *{0}* we need to do the same for ```LabelKeys``` - but do not have a ```Labels``` class, as all translation defaulted to the key name.

- create a new class 'Labels', extended from ```EnumResourceBundle``` 
- implement ```loadMap()```

```
package com.example.tutorial.i18n;

import uk.q3c.krail.core.persist.clazz.i18n.EnumResourceBundle;

public class Labels extends EnumResourceBundle<LabelKey> {
    @Override
    protected void loadMap() {

    }
}
```

- create a new class 'Labels_de' extended from ```Labels```
- put the translations into the map
```
package com.example.tutorial.i18n;

import static com.example.tutorial.i18n.LabelKey.*;

public class Labels_de extends Labels {
    @Override
    protected void loadMap() {
        put(is_selected, "aktiviert ist");
        put(is_not_selected, "nicht aktiviert ist");
        put(Options, "die Optionen");
    }
}
```
- run the application, and:
    - in the Locale selector, top right of the page, select "Deutsch" (the selector takes its selection list from the supported locales you have defined)
    - a popup will inform you, in German, of the change
    - a number, but not all items have changed language (Krail has some translations built in, and these are the ones which have changed. Hopefully, the number of translations will increase over time - if you can contribute, please do)
    - log in and navigate to 'MyNews'
    - most of the page will still be in English (we have not provided translations for it all) but the banner and Options button should now be in German.
    - change the language back to English - and the banner stays in German, while the Options button switches back to English.
    
Why is this happening?  Well, currently there is nothing to tell this view that it should re-write the banner when there is a change in language.  The **@Caption** annotation handles that automatically, but for a manual translation we need to respond to a language change message.
 
- move the logic for populating the banner to its own method
```
private void populateBanner() {
    int temperature = (new Random().nextInt(40)) - 10;
    LabelKey selection = (option.get(ceoVisible)) ? LabelKey.is_selected : LabelKey.is_not_selected;
    bannerLabel.setValue(getTranslate().from(MessageKey.Banner, selection, temperature));
}
```
- ```optionValueChanged()``` should now look like this

```
@Override
public void optionValueChanged(Property.ValueChangeEvent event) {
    ceoNews.setVisible(option.get(ceoVisible));
    itemsForSale.setVisible(option.get(itemsForSaleVisible));
    vacancies.setVisible(option.get(vacanciesVisible));
    populateBanner();
}
```
#CurrentLocale and responding to change

You have been using ```CurrentLocale``` without being aware of it - ```Translate``` refers to it when a call is made to ```Translate.from()```.  A little explanation is now needed.  

```CurrentLocale``` holds the currently selected locale for a user.  It is first populated from a combination of things like Web Browser settings, and whatever you have defined in the ```I18NModule``` - the logic is in described in the ```DefaultCurrentLocale``` javadoc.
  
When a change is made to the current locale (in our case, using the ```LocaleSelector```), ```CurrentLocale``` publishes a ```LocaleChangeBusMessage``` via the session [Event Bus](tutorial-event-bus.md).  We need to intercept that message, and respond to it by updating the banner.

- make this View an event bus listener and subscribe to the session Event Bus

```
@Listener @SubscribeTo(SessionBus.class)
public class MyNews extends Grid3x3ViewBase implements OptionContext {
```
- register a handler for the message - the annotation and the message type are the important parts - the method can be called anything
- call ```populateBanner``` to update its text
```
@Handler
protected void localeChanged(LocaleChangeBusMessage busMessage) {
    populateBanner();
}
```
- Run the application, log in and navigate to 'MyNews'
- Changing locale now immediately updates the banner

#Pattern sources

So far we have used the class-based method for defining I18N patterns.  Krail originally supported the traditional properties files, but that has now been withdrawn as we saw no benefit to using it.  

You can, however, use any source - a database, REST service or any other service which can provide patterns via a pluggable DAO.  Through Guice configuration, each source is identified by an annotation.  Krail provides an in-memory map as a source, annotated with **@InMemory**.  Being in memory, it is not very useful except for testing - later you will see a [JPA implementation](tutorial-persistence-jpa.md))

##Selecting pattern sources
 
Let's add a database source (which for now will actually be an in-memory map, until we [add persistence](tutorial-persistence-jpa.md))

- in ```TutorialI18NModule```, define two pattern sources - class and in-memory (previously we were using the default - class only).  The order they are declared is significant, as that is also the order they queried.
```
@Override
protected void define() {
    defaultLocale(Locale.UK);
    supportedLocales(Locale.GERMANY);
    source(InMemory.class);
    source(ClassPatternSource.class);
   
}
```
- The ```DefaultBindingManager.addPersistenceModules()``` defines a default, in-memory store with a PatternDao implementation - no changes are therefore needed to ```BindingManager``` to include this. 

 
If you were to run the application now, nothing will have changed.  We have set the order of bundle sources so that "in-memory store" is queried first - of course nothing will be found as it is empty - and the "class", which will return the same as before.

To prove this works, we need to put a value in to the in-memory store:

- in 'MyNews' add ```PatternSource``` and a provider for ```PatternDao```.  Note the **@InMemory** annotation on ```PatternDao```. 

We do not generally need to access the ```PatternDao``` directly, except putting values into store - the Krail core takes care of reading patterns from the sources you have defined in the ```I18NModule```
```
@Inject
protected MyNews(Translate translate, Option option, OptionPopup optionPopup, SubjectProvider subjectProvider, UserNotifier userNotifier, @InMemory
                 Provider<PatternDao> patternDaoProvider, PatternSource patternSource) {
    super(translate);
    this.option = option;
    this.optionPopup = optionPopup;
    this.subjectProvider = subjectProvider;
    this.userNotifier = userNotifier;
    this.patternDaoProvider = patternDaoProvider;
    this.patternSource = patternSource;
}
```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">We find that injecting a Dao provider (as opposed to a Dao directly) removes potential issues with persistence sessions, and recommend it as standard practice</p>
</div>

- provide a way to enter a value for one key
    - ```in MyNews.doBuild()``` add the code below
    
```
        i18NTextBox = new TextField();
        i18NTextBox.setCaption("enter a value for LabelKey.is_selected");
        submitButton = new Button("submit");
        PatternCacheKey cacheKeyUK = new PatternCacheKey(LabelKey.is_selected, Locale.UK);
        submitButton.addClickListener(event -> {
            patternSource.clearCache();
            patternDaoProvider.get().write(cacheKeyUK, i18NTextBox.getValue());
            populateBanner();
        });
        FormLayout formLayout = new FormLayout(i18NTextBox, submitButton);
        setTopRight(formLayout);

```

- change the entry for the banner to use only the first two columns (so that we can use the top right cell)

```
   getGridLayout().addComponent(bannerLabel, 0, 0, 1, 0);
```

This provides a ```TextField``` to capture some input, and a submit button to submit the value to the in memory store and update the banner.  The ```PatternSource``` is only needed to clear the cache (to ensure we capture the new value).
  
- Run the application, login and navigate to 'MyNews'
- Make sure that the CEO New Channel is selected (we defined an I18N value for this)
- Enter some text, and press 'submit'
- The banner will update immediately with the text you entered
- change the Locale selector to "Deutsch" and note that the German translation is still used - we only set a value for Locale.UK
 
You may recall that we defined the bundle sources like this, and noted that the declaration order is important:
```
@Override
protected void define() {
   defaultLocale(Locale.UK);
   supportedLocales(Locale.GERMANY);
   source(InMemory.class);
   source(ClassPatternSource.class);
  
}
 
```
This means that the **@InMemory** source is checked first for a value - if there is one, it is used, and the **ClassPatternSource** is not queried.  We just created a value in the in-memory store, so that is the one that is used -this demonstrates is why the order of declaration is important.
 
If you refer to the Javadoc for ```I18NModule``` you will see that there are methods which enable very specific settings for the order of sources.  We will not cover that in this Tutorial, but leave you to experiment.  

#Changing Krail Core values

We have just demonstrated changing the value for a specific key - exactly the same technique can be used to change (or add new languages to) Krail core ```I18NKey```s.  This does require exporting the keys to a bundle source with mutable values (probably a database).  The ```PatternUtility``` class provides methods to support that process.
  
#Methods of configuration revisited

Earlier [in this section](tutorial-i18n-components-validation.md#config-methods) we elected to sub-class ```I18NModule``` as a way of configuring it, resulting in this ```define()``` method:

```
@Override
protected void define() {
    defaultLocale(Locale.UK);
    supportedLocales(Locale.GERMANY);
    source(InMemory.class);
    source(ClassPatternSource.class);
}
```
with this BindingManager entry
```
@Override
protected Module i18NModule() {
    return new TutorialI18NModule();
}   
```

Because the I18NModule methods used are all fluent, we could achieve exactly the same by just changing the ```BindingManager``` like this:
```
@Override
protected Module i18NModule() {
    return new I18NModule().defaultLocale(Locale.UK)
                           .supportedLocales(Locale.GERMANY)
                           .source(InMemory.class)
                           .source(ClassPatternSource.class);
}
```
 
The choice is yours!

 
#Summary

There is still more to cover under the "I18N" heading, so the next section will cover more of how to use Krail's I18N with Vaadin components.  In this section we have:

- used ```Translate``` to translate an ```I18NKey``` directly
- translated a message with parameters
- created a **@Caption** annotation for use with your own ```I18NKey```s
- added support for an additional language
- been introduced to the ```CurrentLocale``` class
- seen how to respond to a change of Locale message from the Event Bus
- set up a new bundle source, and determined the order of querying sources
- cleared the pattern cache
- configured Guice modules fluently and directly

#Download from GitHub
To get to this point straight from GitHub, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step08**
 



