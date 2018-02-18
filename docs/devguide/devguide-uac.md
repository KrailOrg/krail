# User Access Control

So what actually happens?

Krail has a ```MasterSitemap```, which contains all the page definitions for the whole site.  This is built from the page definitions you provide using either the direct method or annotation method you covered in [Tutorial - Pages and Navigation](../tutorial/tutorial-pages-navigation.md).
  
When a user logs in, the ```MasterSitemap``` is copied to a user-specific instance of ```UserSitemap```.  However, only those pages which the user is authorised to see are actually copied across, and displayed in the navigation components.  This means that either the pages must be public, or the user must have permissions to see them in order for them to be displayed.

During the process of copying from the ```MasterSitemap``` to the UserSitemap, each page is checked to see whether the user has permission to view it - if not, then it is not copied to the ```UserSitemap```.  This provides one layer of security, and it also means that any attempt by a user to access a url not in the UserSitemap is rejected with a "page does not exist" message, not a "page is not authorised". 

