#Introduction

In the previous section we covered the use of options, and mentioned that Krail sees Options as the [top layer of configuration](devguide01.md).

So that means the Tutorial has so far covered the top layer and part of the bottom layer.  The "bottom layer" includes the configuration we have done using Guice and Guice modules, but there is a little more to cover on that [later](tutorial16.md).
  
The middle layer is the one provided by the facility to load ini files (and other formats), and that is what we will explore in this section

#Overview

Krail integrates [Apache Commons Configuration](https://commons.apache.org/proper/commons-configuration/) to provide support for this form of loading configuration, which extends well beyond just ini files.  (See the Apache documentation for more information).   

More specifically, Krail captures configuration information in an instance of ```ApplicationConfiguration```, which allows a set of configuration values to override a previous set (when they have the same property names).  This is similar in principle to the way [options](tutorial05.md) work.

#Example

By default, Krail looks for a file krail.ini in WEB-INF:

- create a file 'krail.ini' in *src/main/webapp/WEB-INF*
- you may be familiar with the extended properties file format .... populate it with:
```
[tutorial]
quality=good
```
- In the 'pages' package create a new view, 'IniConfigView', extended from ```Grid3x3View``` 
- Override the ```doBuild()``` method (you will get some compile errors)
```
  @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        Button showConfigButton = new Button("Show config");
        tutorialQualityProperty = new Label();
        showConfigButton.addClickListener(event -> showConfig());
        setTopCentre(tutorialQualityProperty);
        setMiddleCentre(showConfigButton);
        getGridLayout().setComponentAlignment(tutorialQualityProperty, Alignment.MIDDLE_CENTER);
    }
```
All this does is set up a button to show the config, and a label to display it.  Loading the config into an instance of ```ApplicationConfiguration``` actually happens at application startup. 

- inject ```ApplicationConfiguration``` (this is a singleton, and therefore just one instance for the whole application)
```
    @Inject
    protected IniConfigView(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }
```
- implement the ```showConfig()``` method (which we could also put in to the lambda)

```
private void showConfig() {
    tutorialQualityProperty.setValue("Tutorial quality is: " + applicationConfiguration.getString("tutorial.quality"));
}
```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">Note the syntax of <i>tutorial.quality</i> - [section name].[property name] - Apache Commons Configuration provides this 'FQN' type syntax - see the Apache documentation for more information </p>
</div>

- Include this new page in ```MyOtherPages```

```
addEntry("ini-config", IniConfigView.class, LabelKey.Ini_Config, PageAccessControl.PUBLIC);
```

#More layers

There may be occasions when adding libraries to an application that further configuration is needed.  You can add as many configuration files as you require.    

##Adding another ini file

- create another file 'moreConfig.ini' in WEB-INF, with this content:
```
[tutorial]
quality=brilliant
completed=false

[connection]
timeout=1000
```
This will be used to show a property overriding another, while also adding new properties.  To display the values

- add Vaadin components to ```doBuild()```:
```java
    connectionTimeoutProperty = new Label();
    tutorialCompletedProperty = new Label();
    setTopRight(tutorialCompletedProperty);
    setTopLeft(connectionTimeoutProperty);
```
- include them in ```showConfig()```
```
private void showConfig() {
    tutorialQualityProperty.setValue("Tutorial quality is: " + applicationConfiguration.getString("tutorial.quality"));
    tutorialCompletedProperty.setValue(applicationConfiguration.getString("tutorial.completed"));
   connectionTimeoutProperty.setValue("The timeout is set to: " + applicationConfiguration.getString("connection.timeout"));
}`

```

We now need to set up the Guice configuration so it knows about the additional file.  This will be made easier once [issue 416](https://github.com/davidsowerby/krail/issues/416) has been completed, but for now we need to sub-class the appropriate Guice module, and then tell the ```BindingManager``` about it

- create a new package *com.example.tutorial.ini*  
- in this package, create a new class "TutorialIniConfigModule" and extend ```ApplicationConfigurationModule```
- add the config for *moreConfig.ini*
```
package com.example.tutorial.ini;

import uk.q3c.krail.core.config.ApplicationConfigurationModule;

public class TutorialIniConfigModule extends ApplicationConfigurationModule {
  @Override
    protected void bindConfigs() {
        super.bindConfigs();
        addConfig("moreConfig.ini",1,false);
    }
}
```

Be aware that the order that the files are processed is important if they contain the same (fully qualified) property names. If you look at the javadoc for ```addConfig()``` you will see that the second parameter determines the order (priority) of loading.

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">The ordering of priority for ApplicationConfiguration is the reverse of that used by Option (where 0 = highest).  This is confusing and will be changed by #415</p>
</div>

- update the BindingManager to use the new module

```
@Override
protected Module applicationConfigurationModule() {
    return new TutorialIniConfigModule();
}
```
- Run the application and select the "Ini Config" page
- Press "Show config" and you will see the values provided by *krail.ini* and *moreConfig.ini* combined:

    - *tutorial.completed* from *krail.ini* is unchanged as there is no value for it in *moreConfig.ini*
    - *connection.timeout* is a new property from *moreConfig.ini*
    - *tutorial.quality* from *krail.ini* has been overridden by the value in *moreConfig.ini*
    
#Fail early
If an ini file is essential for the operation of your application, ```addConfig()``` allows you to specify that.  Both the examples have the 'optional' parameter set to 'false', but of course both files are present.
 
- add another entry to ```TutorialIniConfigModule```, but do not create the corresponding file
```
    addConfig("essential.ini",2,false);
```
- run the application and it will fail early with a ```FileNotFoundException```

- remove the line you just added, to clean up

The final versions of the files should be:

```
package com.example.tutorial.ini;

import uk.q3c.krail.core.config.ApplicationConfigurationModule;

public class TutorialIniConfigModule extends ApplicationConfigurationModule {

    @Override
    protected void bindConfigs() {
        super.bindConfigs();
        addConfig("moreConfig.ini",1,false);
    }
}
```
```
package com.example.tutorial.app;

import com.example.tutorial.ini.TutorialIniConfigModule;
import com.example.tutorial.pages.AnnotatedPagesModule;
import com.example.tutorial.pages.MyOtherPages;
import com.example.tutorial.pages.MyPages;
import com.example.tutorial.pages.MyPublicPages;
import com.google.inject.Module;
import uk.q3c.krail.core.guice.DefaultBindingManager;
import uk.q3c.krail.core.navigate.sitemap.SystemAccountManagementPages;
import uk.q3c.krail.core.sysadmin.SystemAdminPages;
import uk.q3c.krail.core.ui.DefaultUIModule;
import com.example.tutorial.i18n.LabelKey;

import java.util.List;

public class BindingManager extends DefaultBindingManager {

    @Override
    protected void addAppModules(List<Module> baseModules) {

    }

    @Override
    protected Module servletModule() {
        return new TutorialServletModule();
    }

    @Override
    protected void addSitemapModules(List<Module> baseModules) {
        baseModules.add(new SystemAccountManagementPages());
        baseModules.add(new MyPages().rootURI("private/finance-department"));
        baseModules.add(new AnnotatedPagesModule());
        baseModules.add(new SystemAdminPages());
        baseModules.add(new MyPublicPages());
        baseModules.add(new MyOtherPages());
    }

    @Override
    protected Module uiModule() {
        return new DefaultUIModule().uiClass(TutorialUI.class)
                                    .applicationTitleKey(LabelKey.Krail_Tutorial);
    }


    @Override
    protected Module applicationConfigurationModule() {
        return new TutorialIniConfigModule();
    }
}
```
```
package com.example.tutorial.pages;

import com.google.inject.Inject;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import uk.q3c.krail.core.config.ApplicationConfiguration;
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class IniConfigView extends Grid3x3ViewBase {

    private ApplicationConfiguration applicationConfiguration;
    private Label tutorialCompletedProperty;
    private Label tutorialQualityProperty;
    private Label connectionTimeoutProperty;


    @Inject
    protected IniConfigView(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        Button showConfigButton = new Button("Show config");
        tutorialQualityProperty = new Label();
        connectionTimeoutProperty = new Label();
        tutorialCompletedProperty = new Label();
        showConfigButton.addClickListener(event -> showConfig());
        setTopCentre(tutorialQualityProperty);
        setMiddleCentre(showConfigButton);
        setTopRight(tutorialCompletedProperty);
        setTopLeft(connectionTimeoutProperty);
        getGridLayout().setComponentAlignment(tutorialQualityProperty, Alignment.MIDDLE_CENTER);
        getGridLayout().setComponentAlignment(tutorialCompletedProperty, Alignment.MIDDLE_CENTER);
        getGridLayout().setComponentAlignment(connectionTimeoutProperty, Alignment.MIDDLE_CENTER);
    }

    private void showConfig() {
        tutorialQualityProperty.setValue("Tutorial quality is: " + applicationConfiguration.getString("tutorial.quality"));
        tutorialCompletedProperty.setValue(applicationConfiguration.getString("tutorial.completed"));
        connectionTimeoutProperty.setValue("The timeout is set to: " + applicationConfiguration.getInt("connection.timeout"));
    }
    
}
```
#Summary

- We have loaded an ini file
- we have modified the values in one ini file with those from another
- We have demonstrated ensuring an early fail if a file is missing
 
Apache Commons Configuration supports much more than just ini files, and can support [variety of sources](https://commons.apache.org/proper/commons-configuration/userguide_v1.10/overview.html#Configuration_Sources) - Krail will just accept anything that Apache Commons Configuration provides
  
#Download from Github
To get to this point straight from Github, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step06**
