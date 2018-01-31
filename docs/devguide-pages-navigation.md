# Navigation Overview

The Vaadin UI classes, Views and Navigator are all closely involved in the process of navigation.  The Vaadin 7 handbook gives a good description of how it works, but Krail brings some additional features to it.

In brief, the UI class represents a browser tab, and a View is placed within the UI.  The View is then changed in response to navigation changes.  You can have multiple UI classes, and multiple View classes.  In Krail, the selection of which View to display is derived from the URI.

## When to use a UI or View

One question which arises quite quickly is what should be part of a UI, and what should be in a View.  For example a header bar could go in either.  Our inclination is to use a UI containing only elements which will always appear on every page. Most of the user interface is then provided through the View.

Krail makes the use of Views even easier, and as a result probably makes the use of the UI class to hold user interface components less useful.  

# URI and Page

A central part of the way navigation works in Krail is the interpretation of the [URI](glossary.md#URI).  The default implementation of ```URIFragmentHandler```, is ```StrictURIFragmentHandler``` and it defines the URI with the following example:

This provides a more strict interpretation of the UriFragment than Vaadin does by default. It requires that the URI
structure is of the form:

*http://example.com/domain#!finance/report/risk/id=1223/year=2012 (with or without the bang after the hash,depending on the useBang setting)* 

where: 

*finance/report/risk/* 

is a "virtual page path" and is represented by a View 

and everything after it is paired parameters. If a segment within what should be paired parameters is malformed, it is ignored, and when the URI is reconstructed, will disappear. So for example: 

*http://example.com/domain#!finance/report/risk/id=1223/year2012*
 
would be treated as: 

*http://example.com/domain#!finance/report/risk/id=1223* 

The year parameter has been dropped because it has no "=" 

Optionally you can use hash(#) or hashBang(#!). Some people get excited about hashbangs. Try Googling it

