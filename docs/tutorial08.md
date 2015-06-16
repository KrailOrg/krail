#Introduction

We had a very [brief introduction](tutorial02.md#I18NIntro) to Krail's implementation of I18N in an earlier section of the Tutorial, and this gave us the ```LabelKey``` and ```DescriptionKey``` **enum** classes.

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
- first, inject ```Translate``` into the constructor

```
@Inject
public MyNews(Option option, OptionPopup optionPopup, Translate translate) {
    this.option = option;
    this.optionPopup = optionPopup;
    this.translate = translate;
}
```  
- In ```doBuild()```, replace:
```java
popupButton = new Button("options");
```

with

```
popupButton = new Button(translate.from(LabelKey.Options));

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
getGridLayout().addComponent(bannerLabel,0,0,2,0);
```




So far, all the I18 patterns have been simple - they have had no parameters.  Now we want a more complex message with some dynamic elements to it.

Krail core uses a convention which splits what could be a very long list of keys like this:

- Labels : short, usually one or two words, no parameters, generally used as captions
- Descriptions : longer, typically several words, no parameters, generally used in tooltips
- Messages : contains parameter(s). 


This is just a convention - which we will use in this Tutorial - but it is entirely your decision how you organise your keys.

###Creating a Key class

Assuming you have followed this Tutorial from the start, you have already seen how to [create a key class](tutorial02.md#I18NIntro).  We are going to add another now:

- in the 'com.example.tutorial.i18n' package, create an Enum class called 'MessageKey'.  It should implement the ```I18NKey``` interface
- create a MessageKey constant **Banner**

```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NKey;

public enum MessageKey implements I18NKey {
    Banner
}

```


This is going to be a long message, and because it has parameters, the default translation cannot be taken from the key name.  We will use a class based method for defining the pattern: 

- in the 'com.example.tutorial.i18n' package, create a class 'Messages'
- override the ```loadMap()``` method
```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.EnumResourceBundle;

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

import uk.q3c.krail.i18n.EnumResourceBundle;

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
LabelKey selection = (option.get(ceoVisible).booleanValue()) ? LabelKey.is_selected : LabelKey.is_not_selected;
Label bannerLabel = new Label(translate.from(MessageKey.Banner,  selection, temperature));
getGridLayout().addComponent(bannerLabel,0,0,2,0);
```

Parameters passed as ```I18NKey``` constants are also translated.  These are currently the only parameter types that are localised, see [open ticket](https://github.com/davidsowerby/krail/issues/428).

- Run the application, log in and and navigate to "MyNews" (login = 'eq', 'eq'), 
    - the banner has been expanded to include the variable values
- click on "options" and change the value for CEO New Channel - but the label does not change.
- To fix this
    - make ```bannerLabel``` a field
    - move the code to set the bannerLabel value to ```optionValueChanged```
    
in ```doBuild()``` we now have:
    
```
bannerLabel = new Label();
getGridLayout().addComponent(bannerLabel,0,0,2,0);
```

    
while ```optionValueChanged()``` is now:
    
```
@Override
public void optionValueChanged(Property.ValueChangeEvent event) {
    ceoNews.setVisible(option.get(ceoVisible));
    itemsForSale.setVisible(option.get(itemsForSaleVisible));
    vacancies.setVisible(option.get(vacanciesVisible));
    int temperature = (new Random().nextInt(40))-10;
    LabelKey selection = (option.get(ceoVisible).booleanValue()) ? LabelKey.is_selected : LabelKey.is_not_selected;
    bannerLabel.setValue(translate.from(MessageKey.Banner,  selection, temperature));
}
```




#Translation from Annotations

Especially when using Vaadin components, it is often more convenient to use an ```Annotation``` instead of calling ```Translate``` directly - this keeps the ```I18NKey```s with the fields using them.

To achieve this, we need an annotation that is specific to our ```I18NKey``` implementations (this is because of the limitations Java places on ```Annotation``` parameters)

- in the package 'com.example.tutorial.i18n', create a new Annotation class called "Caption".  Note the ```@I18NAnnotation``` - this tells Krail's ```I18NAnnotationProcessor``` that this annotation is used for I18N. 

```java
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NAnnotation;

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

Naturally, you cannot use variable values with an annotation - by its very nature, ```Annotation``` will only take static values. For anything dynamic, therefore, you will need to use a direct call to ```Translate```.


#Multi-Language

The whole point of I18N is of course to support multiple languages / Locales.  This section assumes that you are familiar with the standard Java approach to I18N.  For those not familiar with it, there are many online resources if you need them.

##Supported Locales

Krail uses the ```I18NModule``` to define which Locals are supported

#Changing Krail's Key Values

we will, however, defer the use of a database for holding I18N values until the [next section](tutorial09.md#persistence-i18n), when we look at persistence in general.