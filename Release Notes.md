### Release Notes for krail 0.9.3

This version upgrades to Vaadin 7.4.6 and Guice 4.0.  The I18NProcessor has been updated to reduce the amount it has to process, and clarify when drill down occurs.  The amount of setting up for a simple application has been reduced.  

#### Change log

-   [256](https://github.com/davidsowerby/krail/issues/256): Add position index to Sitemap loaders
-   [370](https://github.com/davidsowerby/krail/issues/370): Locale changes fired before UI components constructed
-   [371](https://github.com/davidsowerby/krail/issues/371): ViewBase to implement beforeBuild
-   [372](https://github.com/davidsowerby/krail/issues/372): Widgetset not compiling
-   [373](https://github.com/davidsowerby/krail/issues/373): Exclude pages from navigation
-   [375](https://github.com/davidsowerby/krail/issues/375): Deprecate file based sitemap definition
-   [378](https://github.com/davidsowerby/krail/issues/378): Guice 4.0
-   [379](https://github.com/davidsowerby/krail/issues/379): Vaadin 7.4.5
-   [382](https://github.com/davidsowerby/krail/issues/382): Navigator.navigateTo(navigationState) throws NPE
-   [383](https://github.com/davidsowerby/krail/issues/383): Fluent interface for NavigationState
-   [384](https://github.com/davidsowerby/krail/issues/384): ID.getId() and getIdc() inconsistent for enhanced classes
-   [386](https://github.com/davidsowerby/krail/issues/386): ViewBase should annotate rootComponent @I18N
-   [386](https://github.com/davidsowerby/krail/issues/387): I18NProcessor misses enhanced objects
-   [388](https://github.com/davidsowerby/krail/issues/388): Vaadin 7.4.6
-   [389](https://github.com/davidsowerby/krail/issues/389): I18N processing - handling of container components


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.12
   test compile dependency version changed to: q3c-testUtil:0.7.9

#### Detail

*Placeholder for documentation*


---
*Updated version info*


---
*Fix [388](https://github.com/davidsowerby/krail/issues/388) Vaadin 7.4.6*


---
*Fix [378](https://github.com/davidsowerby/krail/issues/378) Guice 4.0*


---
*Fix [379](https://github.com/davidsowerby/krail/issues/379) Vaadin 7.4.5*


---
*Updated javadoc and corrected key detection*

processTable and processGrid were looking for a LabelKey in columns - corrected to I18NKey


---
*Bintray upload changes*

dryRun=true by default (set by krail-master), unless overridden by individual projects


---
*Updated javadoc and corrected key detection*
  
processTable and processGrid were looking for a LabelKey in columns - corrected to I18NKey

---
*See [391](https://github.com/davidsowerby/krail/issues/391)  Options added to DefaultApplicationUI*
    
Limited usefulness until there is a way enabling the user to select what they want

---
*Adding docs folder and MkDocs file*

---
*Various I18N improvements*

The method for identifying fields with annotations has been simplified, and reduces the number of fields unnecessarily accessed.  Drill down options have been removed from all I18N annotations apart from @I18N itself.  No drill down now occurs unless a field or class is explicitly marked with @18N.
   
Fix [386](https://github.com/davidsowerby/krail/issues/386) Decided not to do this - the rules for drill down are clearer now, and typically the rootComponent is just a layout - the fields may well be held in a View
   
Fix [387](https://github.com/davidsowerby/krail/issues/387) Separated the identification of the class for the I18NProcessor target into I18NHostClassIdentifier.  The current default implementation only identifies Guice AOP enhanced classes, but could be easily extended for javassist and cglib etc
    
Fix [389](https://github.com/davidsowerby/krail/issues/389) Decided this does not actually work well in practice.  As now implemented, drill down only occurs if a component has an explicit @18N annotation (default is drillDown=true).

---
*See [283](https://github.com/davidsowerby/krail/issues/283).  loadData() was called even when out of focus
    
Was using the UIBus to trigger a call to loadData, but that means that all views that have been created wihtin a UIScope would be called to load data, even if they are no longer active.  Modified to use direct interface call (ultimately from Navigator) so that load data is only called when the view becomes active.

---
*See [283](https://github.com/davidsowerby/krail/issues/283) ViewBase reconstructs components only when needed*
     
ViewBase delegates to a doBuild for sub-classes to implement, and uses a flag to decide whether a rebuild is necessary or not.  This removes the situation where the field components are reconstructed every time a View is selected.  It is still possible to force a rebuild by clearing the flag in the beforeBuild() method.
     
At loadData() method has been added, and is called by a @Handler for the AfterViewChangeBusMessage - it is empty by default, and may not be needed when a view is based on Vaadin containers.
     
The unnecessary translation of keys has not yet been addressed.
     
---
*See [374](https://github.com/davidsowerby/krail/issues/374) Changes to ScopedUIProvider to reduce new application setup*
    
ScopedUIProvider used to require sub-classing for any application which chose to implement its own UI, and the new sub-class required binding in a sub-class of DefaultUIModule.  There is now a simpler way to bind the UI class to the underlying Vaadin UIProvider with a one line change in a sub-class of  DefaultUIModule.  Sub-classing of ScopedUIProvider is now only required for applications which use multiple UI classes
    
---
*Fix [384](https://github.com/davidsowerby/krail/issues/384) ID inconsistently applied when class is enhanced*
    
An enhanced class name contains, for example, $$EnhancerByGuice3f556345 when taken from an instance.  This meant that ID.getId() and ID.getIdc()  return different results for an enhanced class.  Fixed by using ClassnameUtil to return the class name without the enhanced portion.
Also, DefaultErrorView logs the error as well as displaying it

---
*Fix [383](https://github.com/davidsowerby/krail/issues/383) Fluent interface added to NavigationState*

---
*Fix [382](https://github.com/davidsowerby/krail/issues/382) Navigator.navigateTo updates fragment*
    
When Navigator.navigateTo(NavigationState) is called, the NavigationState is always updated using URIFragmentHandler.updateFragment.  This means the caller does not have to remember to do so, and also means that the calling method may not then need to inject URIFragmentHandler to do the update.

---
*Removed unused LabelKey and corrected test*

---
*Option added to DefaultApplicationUI*

---
*Fix [373](https://github.com/davidsowerby/krail/issues/373) Exclude pages from navigation*
     
 The positionIndex for pages is utilised for identifying pages which should not be displayed in navigation components. A positionIndex of < 0 is excluded.  NoNavFilter is used for exclusion during the process of loading the navigation components.
     
 UserSitemapNode now carries a copy of the positionIndex (as opposed to referencing the MasterSitemapNode), to offer the developer the potential to provide a different presentation order in navigation components, depending on some characteristic of the user.
    
 --- 
*Fix [256](https://github.com/davidsowerby/krail/issues/256) Position index added to Sitemap loaders*
     
The Annotation and Direct sitemap modules and loaders handle a position index for pages which allows the order of pages to be specified (the position index is relative, that is, it is used to sort the page order and it does not therefore matter whether there are gaps in numbering). The File based sitemap has been deprecated and therefore not modified
 
 ---
*Fixed test*
    
 Test failed as a result of changes for #374
 
 ---
 *Fix [375](https://github.com/davidsowerby/krail/issues/375) File based Sitemap loading deprecated
  
 ---
*Fix [371](https://github.com/davidsowerby/krail/issues/371) ViewBase implements beforeBuild()*
    
ViewBase provides an empty implementation of beforeBuild(), as this method is rarely used.  Removed empty implementations from existing Views.
    
---
*See [374](https://github.com/davidsowerby/krail/issues/374) UIModule provides defaults*
     
UIModule was previously abstract and had to be sub-classed for every application.  It has been renamed DefaultUIModule, and uses DefaultApplicationUI to remove the need for sub-classing.  Other potential areas for simplification still to be looked at include the reworking of ScopedUIProvider so that it does not need to be sub-classed for a different UI class;  the application title is currently specified in DefaultUIModule - perhaps this should be moved to the ServletModule, as that has to be defined for each application.
 
---
*Fix [372](https://github.com/davidsowerby/krail/issues/372) Change validation API version*
     
This also removes the unwrap method of Krail's SimpleContext.
 
---
*Fix [370](https://github.com/davidsowerby/krail/issues/370) Scoped UI init order*
     
CurrentLocale was being set up from the current environment before the UI had been constructed - this only occurred when the UI deferred component construction to the doLayout method, as opposed to doing it in its own constructor.  The order has been changed in the ScopedUI.init() method so that locale changes do not happen until after the layout has been completed.

---
*DefaultUIModule added*
     
When a new application is being created, this enables the use of DefaultApplicationUi with reduced setup configuration

---

