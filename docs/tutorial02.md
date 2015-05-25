#Adding pages

Clearly we will want to add some new pages, but first we must know what constitutes the definition of a page

##Defining a Page

A page is represented by a URI, which maps to a specified View.  When the name of the page is presented in a navigation aware component, that name must be Locale sensitive.  Once the page is defined, it becomes part of the Krail ```Sitemap```, which forms the heart of the navigation system.  

There are two ways to add pages to Krail and make use of the [navigation features](devguide02.md), and you can use either one, or both. These are the "direct" method or "annotation" method. (Note: there was a third method using an external file but that is now deprecated).  We will use both current methods.

Because the page name is locale sensitive, we will need to provide I18N support.

##Introducing I18N

You may think that it is premature to be considering I18N at this stage - especially if you are writing an application which will only use one language. However, as Krail treats I18N as a first class citizen, and you will find the result of these steps surprisingly useful even in a single Locale application. You could read the [full I18N description](devguide03.md) now, or just follow these steps, as we will come back to I18N later in the Tutorial.  

###Create an I18N Annotation

- create a package 'i18n', under 'com.example.tutorial'
- create two Enum classes, one called 'LabelKey' and one called 'DescriptionKey'.  Each should implement the ```I18NKey``` interface

```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NKey;

public enum LabelKey implements I18NKey {
}

```

```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NKey;

public enum DescriptionKey implements I18NKey {
}
```
 
 
- create a new Annotation class called "Caption".  Note the ```@I18NBindingAnnotation``` - this tells Krail that this annotation is used for I18N. 
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

The methods are used by Krail's annotation scanner to capture I18N keys used to look up locale-sensitive values.

The names of these classes can be anything, it is the ```@18NBindingAnnotation``` and ```I18NKey``` interface which are relevant to function.

This is all we need for our I18N integration for now, so we can get on with adding pages.

##Add a Page - direct method

The "direct" method simply means pages are defined directly in a Guice module.  We will start by adding some private pages ("private" means they will be available only to authorised users).
  
- To keep our pages separate, create a package 'pages', under 'com.example.tutorial'
- Create a class 'MyPages' and extend it from ```DirectSitemapModule``` and provide 
- implement the abstract ```define()``` method 
```
package com.example.tutorial.pages;

import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;

public class MyPages extends DirectSitemapModule{

    @Override
    protected void define() {

    }
}

```
We will use the ```define()``` method to provide our page definitions.  We will create three pages, one at the the site root, with two sub-pages, which we want to look something like this in the navigation tree:

>-Finance<br>
-- Accounts<br>
-- Payroll<br>

- enter the following in the ```define()``` method

```
package com.example.tutorial.pages;

import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;
import uk.q3c.krail.core.shiro.PageAccessControl;
import com.example.tutorial.i18n.LabelKey;

public class MyPages extends DirectSitemapModule{

    @Override
    protected void define() {
        addEntry("finance", FinanceView.class, LabelKey.Finance,
                PageAccessControl.PERMISSION);
        addEntry("finance/accounts", AccountsView.class, LabelKey.Accounts,
                PageAccessControl.PERMISSION);
        addEntry("finance/payroll", PayrollView.class, LabelKey.Payroll,
                PageAccessControl.PERMISSION);
    }
}
```
Make sure you get the right LabelKey - there is one in Krail core as well.  

You will have compile errors, but let's look at what these entries mean.  

- The first parameter is the URI segment, and we generally keep to all lowercase.  The second and third entries are subpages, so need a qualified path.
- The second parameter is the class to use as a View - we haven't created them yet.
- The third parameter is the page name, is locale-sensitive and therefore an I18NKey
- The fourth parameter determines what sort of access control is applied to the page.  We want "private" pages, so they are set to PERMISSION 

Let's make it easier by to create the views by using a single base class, extended from ViewBase.   

- create GridViewBase, extending ViewBase (all it does is position a label in the centre of the page, showing the simple class name of the view - good enough at this stage)

```
package com.example.tutorial.pages;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class GridViewBase extends ViewBase {
    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        Label label = new Label("This is the " + this.getClass()
                                                     .getSimpleName());
        label.setHeight("100px");
        GridLayout grid = new GridLayout(3, 3);

        grid.addComponent(label, 1, 1);
        grid.setSizeFull();
        grid.setColumnExpandRatio(0, 0.33f);
        grid.setColumnExpandRatio(1, 0.33f);
        grid.setColumnExpandRatio(2, 0.33f);

        grid.setRowExpandRatio(0, 0.4f);
        grid.setRowExpandRatio(1, 0.2f);
        grid.setRowExpandRatio(2, 0.4f);

        label.setSizeFull();
        setRootComponent(grid);

    }
}

```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">Extending ViewBase is usually the easiest way to create your views, but however you do it, you must implement <code>KrailView</code></p>
</div>

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">You must always provide a root component for a Krail view.  When extending ViewBase, call <code>setRootComponent()</code> as shown above.</p>
</div>
 
- create the 3 views we want ... AccountView, FinanceView and PayrollView ... just by extending ```GridViewBase``` (only FinanceView is shown here):

```
package com.example.tutorial.pages;

public class FinanceView extends GridViewBase {
}
```

###Defining the I18NKeys
By default, if Krail's ```I18NProcessor``` cannot find a value of an ```I18NKey```, it uses the name of the enum instead, with underscores replaced with spaces.  This means that as long as you are comfortable with breaking the convention for enum constant names (all uppercase), you can provide a lot of what you need without defining any values for the I18NKeys.  This is great for prototyping, and even if your application uses a language with accents and diacriticals, the enum name may be good enough for a prototype.  

- Add the required constants to LabelKey
```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NKey;

public enum LabelKey implements I18NKey {
    Accounts, Payroll, Finance
}
```
##View the Pages
Now that you have some of your own pages, run the application again.  When the application starts the new pages will not be visible - but that is what we should expect, as we said these pages needed permission to view.

- Log in, and you will see the pages, under 'Private', in the navigation tree and menu. 

You may be wondering whether these pages need to be under the 'Private' branch.  At the moment they do, but only becuase of the very simple access control rules supplied by ```DefaultRealm```

##Add a Page - Annotation method
The second method of defining a page is to use an annotation on a ```KrailView``` implementation.  To begin with, we need to tell Krail where to look for annotated views - this reduces the amount of scanning Krail has to do at start up.  To do that we:

- create a new class in the 'pages' package, "AnnotatedPagesModule" and extend ```AnnotationSitemapModule```
- implement the ```define()``` method
- add an entry in the define method, as below:
```
package com.example.tutorial.pages;

import com.example.tutorial.i18n.LabelKey;
import uk.q3c.krail.core.navigate.sitemap.AnnotationSitemapModule;

public class AnnotationPagesModule extends AnnotationSitemapModule {
   
    @Override
    protected void define() {
        addEntry("com.example.tutorial.pages",LabelKey.Accounts);
    }
}
```
The call to addEntry tells Krail to scan the whole of the *com.example.tutorial.pages* package for classes with a @View annotation.  Multiple entries can be made.  The second parameter should be an ```I18NKey``` from the same enum that you are going to use in your @View annotations.  The value you supply to the addEntry method is just a sample, it just needs to be from the same class. This is necessary because of the limitations on what Java allows as Annotation parameter types

Now that this is done, any views in the 'pages' package, annotted with @View, will be added to the Sitemap.

- create another view, "PurchasingView" in the pages package, sub-classed from ```GridViewBase```:

```
package com.example.tutorial.pages;

import uk.q3c.krail.core.navigate.sitemap.View;
import uk.q3c.krail.core.shiro.PageAccessControl;

@View(uri = "private/finance/purchasing",pageAccessControl = PageAccessControl.PERMISSION,labelKeyName = "Purchasing")
public class PurchasingView extends GridViewBase {


}
```

- create the 'Purchasing' constant for ```LabelKey```
```
public enum LabelKey implements I18NKey {
    Accounts, Payroll, Finance, Purchasing
}
```
- tell the ```BindingManager``` to include the module we have just created 

```
 @Override
    protected void addSitemapModules(List<Module> baseModules) {
        baseModules.add(new SystemAccountManagementPages());
        baseModules.add(new MyPages());
        baseModules.add(new AnnotationPagesModule());
    }
```
Run the application again and you will see that "Purchasing" has been added to the Finance page.  

You can mix Direct and Annotation sitemap entries however you wish, but that can lead to confusion.  There are some System Admin pages in Krail core which may help, but please note that they are in the very early stages of development and will change - possibly completely - and may even be withdrawn.  We will add them now anyway, as it demonstrates the ease of managing blocks of pages. 

- In the ```BindingManager``` add the System Admin pages:
```
protected void addSitemapModules(List<Module> baseModules) {
   baseModules.add(new SystemAccountManagementPages());
   baseModules.add(new MyPages());
   baseModules.add(new AnnotationPagesModule());
   baseModules.add(new SystemAdminPages());
}
```
Now run the application and **log in**, and you will find a System Admin branch and a single page with a report.