#Pages and Virtual Pages

Vaadin is an AJAX environment, so strictly speaking there is only one web page presented to the user.  However, with careful use of the URI parameters, it is easy to present the user with what they would perceive as individual pages, selected by URI.  The URI may also carry parameters for selecting state within that page.   Although, therefore, it is technically incorrect to refer to the end results as pages we do, simply because that is how end users would see it.  The terms "page" and "virtual page" are used interchangeably to describe this.

There is also a Vaadin class Page which is much closer to the technical "web page" - this might cause a little confusion, but it is a class which is generally in the background.

#Sitemap

The Krail Sitemap describes, as you would expect, the structure of the application. However, it is not just a passive output from a site, but an integral part of the application design - it brings together a page URI, its associated View and an I18N key for translating the page title. 

#View

A View is almost as described in the Vaadin handbook - the only difference with a ```KrailView```, as opposed to a standard Vaadin View, is that is modified to work with Krail's Guice enabled navigation.

#URI
Of course there is only one correct definition of 'URI', but in a Krail context it is the way the structure of the URI is interpreted which becomes important.  This interpretation is defined by an implementation of ```URIFragmentHandler```, and Krail's default implementation is ```StrctURIFragmentHandler```.  See the javadoc for that class for a definition of how it separates 'pages' from parameters. 