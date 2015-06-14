#Introduction

We had a very [brief introduction](tutorial02.md#I18NIntro) to Krail's implementation of I18N in an earlier section of the Tutorial, and this gave us the ```LabelKey``` and ```DescriptionKey``` **enum** classes.

In this section we will cover most of the rest of Krail's I18N functionality. 

#Elements of I18N

An complete I18N "transaction" requires the following components:

- a key to identify a pattern
- Locale - specific patterns for as many languages you wish to support
- arguments to populate variables in the pattern (if there are any)
- a method for selecting the the correct language and applying the arguments to the pattern 

##The Key
Assuming you have followed this Tutorial from the start you have already seen how to [create a key class](tutorial02.md#I18NIntro).  We are going to add another now:

- in the 'com.example.tutorial.i18n' package, create an Enum class called 'MessageKey'.  It should implement the ```I18NKey``` interface

```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NKey;

public enum MessageKey implements I18NKey {
}

```
##The pattern

By default, the pattern is taken from the enum constant's name(), with underscores replaced with spaces - this make it great for prototyping.


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