**THIS SECTION MAY NOT BE FULLY UP TO DATE & REQUIRES REVIEW**

#Introduction

Early in the development of Krail it was decided to support I18N as an integral part of the development.  Although many applications only need to support one language, trying to add internationalisation (I18N) later can be a major challenge.   I18N  requires the separation of literal strings into files for translation, but this actually makes good sense even if only one language is required, since it keeps a good separation between the use of messages and the exact wording of them.  In addition, the need for parameterised messages will occur regardless of the number of languages supported - so we concluded that it makes sense to always write an application as if I18N  will be required.  And if one day your single language application suddenly has to go multilingual, the only thing required will be the translations. 

#The Basics

In the context of I18N, each piece of text needs a pattern, optionally with placeholders for variable values, and a key to look up that pattern for one or more Locales - then there needs to be something to bring it all together to find the right pattern for a selected Locale, fill in variable values and provide the result.

##The Pattern

The pattern needs to be of the form:
```
The {1} task completed {0} iterations in {2} seconds
```
Different languages may require the parameters to be  in a different order - the number in the {0} represents the order in which the values should be assigned, so for this example values of 5, "last", 20 will become:

The last task completed 5 iterations in 20 seconds

##The Key

A key in Krail is an enum.  This has many advantages over the usual approach of using String constants, especially when combining modules which may need to define their own keys in isolation from each other.  They are also more refactor-friendly.  An I18N key class must implement the I18NKey interface:
```
public enum LabelKey implements I18NKey {
   Yes, No, Cancel
}
```
A key class represents a "bundle".

##The Bundle

The term "bundle" is used throughout native Java I18N support and Krail uses the term in a similar way.  It represents an arbitrary set of keys and the collection of patterns, of potentially multiple languages, that go with the keys.  An enum implementation I18NKey class therefore represents a set of keys for a bundle.
Bundle Reader

Patterns potentially come from different sources.  Krail supports the property file system used by native Java.  It also provides a class based implementation, and the Krail JPA module provides a database implementation.  All of these - and others if required - implement a BundleReader interface to read a pattern from a file, class, database - perhaps a web service - or wherever the implementation is designed to work with. 


<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">If you have a properties file bundle and a class based bundle of the same fully qualified name, things will get confused, so ensure that they have different names or paths. (There is an open ticket for this, but is considered low priority)</p>
</div>

##Pattern Source

The PatternSource combines inputs from potentially multiple Bundle readers into one source.  This is configurable through I18NModule to query the readers in whatever order is required, if necessary a different order for each Bundle.

##Translate

The Translate class is the final step in bringing the pieces together.  It looks up the pattern for a Locale, via the PatternSource, and combines that with the parameter values it is given.  For the example above the call would be:

translate.from (MessageKey.Task_Completion, Locale.UK, 5, "last", 20)

If Translate cannot find a pattern, it will default to using the key name (with underscores replaced with spaces).  This is useful when prototyping, as the pattern can still be meaningful even if not strictly accurate.  That's why you will find many of the Krail examples break with the convention of using all uppercase for the I18NKey enum constants.

Note that if "last" also need to be translated, Translate will accept and perform a nested translation on an I18NKey (though the nested value cannot have parameters - if that is required, two calls to Translate will be needed)

```java
translate.from (MessageKey.Task_Completion, Locale.UK, 5, LabelKey.last, 20)
```

You do not always have to specify the Locale - the default is to use CurrentLocale.

##Current Locale

The CurrentLocale implementation holds the currently selected locale for the user.  The default implementation checks things like the browser locale and user options to decide which locale to use. CurrentLocale can be injected anywhere it is required, and the Translate class will use it if no specific Locale is supplied when calling the from() method.
Configuration

A number of things can be configured in the I18NModule, part of the Guice based configuration - it is worth checking the javadoc for this.  Some configuration is also available via User Options.

#Managing Keys

To make it just a little easier to find values in what can be a long list, the Krail core uses 3 enum classes to define message patterns:

- Labels : short, usually one or two words, no parameters, generally used as captions
- Descriptions : longer, typically several words, no parameters, generally used in tooltips
- Messages : contains parameter(s). 

Note that this is simply a convention - you can call them whatever you wish. 
 
For each there is enum lookup key class:

- LabelKey,
- DescriptionKey,
- MessageKey.

For a class implementation there needs also to be a corresponding map of value (default names of Labels, Descriptions and Messages) extended from EnumResourceBundle.
For a property file implementation there needs to be a file (or set of files for different languages)

Using enums as I18N keys has some advantages, particularly for type checking and refactoring - but it also has a disadvantage.  Enums cannot be extended. To provide your own keys (which you will unless you only use those provided by Krail) you will need to define your own I18NKey implementation, as described in the Tutorial - Extending I18N.


#Managing Locale

##CurrentLocale

CurrentLocale holds the locale setting for the current VaadinSession.  Once a user has logged in, this becomes the same as holding the locale for a user.  Initially, CurrentLocale holds the locale from the browser.  Once a user logs in, the user's options are checked for a preferred setting.  CurrentLocale also supports listeners to enable notification of change of Locale.  This is done automatically for ScopedUIs, KrailViews and their contained components, but if you use anything else which needs to know of such changes, add them to CurrentLocale as a LocaleChangeListener.

It is also possible to set  the locale for a specific component, using the annotations described below.

#Using I18N with Components

A typical component will need a caption, description (tooltip) and potentially a value.   These need to be set in a way which recognises the correct locale, and potentially to update if a change of locale occurs.

**@Caption**

The @Caption annotation marks a component as requiring translation, and can provide caption and description

```java
@Caption(caption=LabelKey.Yes, description=DescriptionKey.Confirm_Ok)
```

The application UI invokes the ```I18NProcessor``` to perform the translation during initialisation of any components it contains directly.  When a view becomes current, its components are also scanned for **@18N** annotations and translated.  ```I18NProcessor``` also updates the component's locale, so that values are displayed in the correct format.

When ```CurrentLocale``` is changed, any UIs associated with the same VaadinSession are informed, and they each update their own components, and their current view.  When a view is changed, if the current locale is different to that previously used by the view, then the View and its components are updated with the correct translation.

When a field or class is annotated with **@I18N**, the scan drills down to check for more annotations, unless the annotation is on a core Vaadin component (something with a class name starting with 'com.Vaadin') - these clearly cannot contain I18N annotations. and therefore no drill down occurs.

**@Description**

Similar to **@Caption**, but without the caption !

**@Value**

Usually, it is the caption and description which would be subject to internationalisation, but there are occasions when it is a component's value which should be handled this way - a ```Label``` is commonly an example of this. Because the use of value is a little inconsistent in this context it has its own annotation.  

##Multiple annotations

You can apply multiple annotations - but note that if you define the locale differently in the two annotations, the result is indeterminate (that is, it could be either of the two locales that have been set).

##Composite Components and Containers

There are occasions when an object contains components, and may not be a component itself, or possibly just not need translation.

For example, you have a composite component ```MyComposite``` which itself does not need a caption or description  - but it contains components which do.  For these cases, simply annotate it with @18N without any parameters, and ```I18NProcessor``` will scan ```MyComposite``` for any fields which need processing.

If ```MyComposite``` is intended to be re-usable, it would probably be better to annotate the class with **@I18N**, so that it does not need to be annotated each time it is used.

#Extending I18N

Annotation parameters cannot be generics, so will need to provide your own equivalent of **@Caption**, **@Description** and **@Value** to use your keys for annotating components for translation.  The method for doing this is described in the Tutorial - Extending I18N.


#Validation

The messages used in validation can be supported in the same way .. see the Validation section for details.

