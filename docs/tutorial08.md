#Introduction

We had a very [brief introduction](tutorial02.md#I18NIntro) to Krail's implementation of I18N in an earlier section of the Tutorial, and this gave us the ```LabelKey``` and ```DescriptionKey``` **enum** classes.

In this section we will cover most of the rest of Krail's I18N functionality. 

#Elements of I18N

A complete I18N "transaction" requires the following components:

- a key to identify a pattern
- Locale-specific patterns for as many languages you wish to support
- arguments to populate variables in the pattern (if there are any)
- a method for selecting the the correct language and applying the arguments to the pattern 

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

This incredibly simple step gives you refactoring support for the key (and therefore its default translation), *and* prepares for a change of language.

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">You may have noticed that we always use "natural case" for the I18N keys.  This is because it is very easy to get from that to upper or lower case, and it greatly supports early development.  It is much harder to automatically convert from upper case to natural.<br><br>
If your coding standards require absolutely that enum constants must be upper case, you can define a specific value for the key, for each language, which we will show you shortly</p>
</div>





Assuming you have followed this Tutorial from the start you have already seen how to [create a key class](tutorial02.md#I18NIntro).  We are going to add another now:

- in the 'com.example.tutorial.i18n' package, create an Enum class called 'MessageKey'.  It should implement the ```I18NKey``` interface

```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NKey;

public enum MessageKey implements I18NKey {
}

```
Krail core uses a convention which splits what could be a very long list of keys like this:

- Labels : short, usually one or two words, no parameters, generally used as captions
- Descriptions : longer, typically several words, no parameters, generally used in tooltips
- Messages : contains parameter(s). 

This is just a convention - you should arrange keys however is best for your application.

##The pattern

By default, the pattern is taken from the enum constant's name(), with underscores replaced with spaces - this make it great for prototyping.  So far we have only used the simplest form of pattern (a pattern with no parameters) - now we will create one using arguments to fill values. 


##The Pattern

The pattern needs to be of the form:

```
The {1} task completed {0} iterations in {2} seconds
```

Different languages may require the parameters to be in a different order - the number in the {0} represents the order in which the values should be assigned, so for this example values of 5, "last", 20 will become:

```
The last task completed 5 iterations in 20 seconds
```



#Changing Krail's Key Values

we will, however, defer the use of a database for holding I18N values until the [next section](tutorial09.md#persistence-i18n), when we look at persistence in general.