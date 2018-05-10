.. glossary::

   Fragment
      See URI

   Route
      See URI

   Sitemap
      The Krail Sitemap describes, as you would expect, the structure of the application. However, it is not just a passive output from a site, but an integral part of the application design - it brings together a route, its associated View and an I18N key for translating the page title.

   View
      A View is almost as described in the Vaadin handbook - the only difference with a `KrailView`, as opposed to a standard Vaadin View, is that is modified to work with Krail's Guice enabled navigation.

   URI
      Of course there is only one correct definition of 'URI', but in a Krail context it is the way the structure of the URI is interpreted which becomes important. This interpretation is defined by an implementation of `URIFragmentHandler`, and Krail's default implementation is `StrctURIFragmentHandler`. See the javadoc for that class for a definition of how it separates 'pages' from parameters.
      As Krail has evolved, the terminology used to describe various elements of a URI has become a bit confused. This section sets out how it should be - but at the moment, other documentation (and method / field naming) are inconsistent. Hopefully the planned move to Vert.x will not change anything further

      By example:

      URI - *com.example.myapp/#members/detail/id=1* - the whole thing
      baseUri - *com.example.myapp/*#members/detail/id=1
      fragment - com.example.myapp/#**members/detail/id=1**
      route - com.example.myapp/#**members/detail**/id=1 - this assumes the use of ``StrictURIFragmentHandler`` to define parameters
      parameters - com.example.myapp/#members/detail/**id=1**