===============
Page Navigation
===============


Clearly we will want to add some new pages, but first we must know what
constitutes the definition of a page

Defining a Page
===============

A page is represented by a URI, which maps to a specified ``KrailView``
class. The name of the page is presented to the user in navigation aware
components, so that name must be Locale sensitive. Once the page is
defined, it becomes part of the Krail ``Sitemap``, which forms the heart
of the navigation system.

There are two ways to add pages to Krail and make use of the `navigation
features </devguide/devguide-pages-navigation.html>`__, and you can use
either one, or both. These are the "direct" method or "annotation"
method. We will use both methods.

Because the page name is locale sensitive, we will first need to provide
I18N support.

Introducing I18N
================

You may think that it is premature to be considering I18N at this stage
- especially if you are writing an application which will only use one
language. However, Krail treats I18N as a first class citizen, and you
will find the result of these steps surprisingly useful even in a single
Locale application. You could read the `full I18N
description </devguide/devguide-i18n.html>`__ now, or just follow these
steps, as we will come back to I18N later in the Tutorial.

Create an I18N Annotation
-------------------------

-  create a package 'i18n', under 'com.example.tutorial'

-  create two Enum classes, one called 'LabelKey' and one called
   'DescriptionKey'. Each should implement the ``I18NKey`` interface

.. code:: java

    package com.example.tutorial.i18n;
    import uk.q3c.krail.i18n.I18NKey;

    public enum LabelKey implements I18NKey {
    }

.. code:: java

    package com.example.tutorial.i18n;
    import uk.q3c.krail.i18n.I18NKey;

    public enum DescriptionKey implements I18NKey {
    }

The names of these classes can be anything, it is the ``I18NKey``
interface which is important.

This is all we need for our I18N integration for now, so we can get on
with adding pages.

Add a Page - direct method
==========================

The "direct" method simply means pages are defined directly in a Guice
module. We will start by adding some private pages ("private" means they
will be available only to authorised users).

-  To keep our pages separate, create a package 'pages', under
   'com.example.tutorial'

-  Create a class 'MyPages' and extend it from ``DirectSitemapModule``
   and provide

-  implement the abstract ``define()`` method

.. code:: java

    package com.example.tutorial.pages;

    import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;

    public class MyPages extends DirectSitemapModule{

        @Override
        protected void define() {

        }
    }

We will use the ``define()`` method to provide our page definitions. We
will create three pages, one at the the site root, with two sub-pages,
which we want to look something like this in the navigation tree:

::

    > Finance
    >> Accounts
    >> Payroll

-  enter the following in the ``define()`` method

.. code:: java

    package com.example.tutorial.pages;

    import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;
    import uk.q3c.krail.core.shiro.PageAccessControl;
    import com.example.tutorial.i18n.LabelKey;

    public class MyPages extends DirectSitemapModule{

        @Override
        protected void define() {
            addEntry("private/finance", FinanceView.class, LabelKey.Finance,
                    PageAccessControl.PERMISSION);
            addEntry("private/finance/accounts", AccountsView.class, LabelKey.Accounts,
                    PageAccessControl.PERMISSION);
            addEntry("private/finance/payroll", PayrollView.class, LabelKey.Payroll,
                    PageAccessControl.PERMISSION);
        }
    }

Make sure you get the right LabelKey - there is one in Krail core as
well.

You will have compile errors, but let’s look at what these entries mean.

-  The first parameter is the URI segment, and we generally keep to all
   lowercase. The second and third entries are subpages, so need a
   qualified path.

-  The second parameter is the class to use as a View - we haven’t
   created them yet.

-  The third parameter is the page name, is locale-sensitive and
   therefore an ``I18NKey``

-  The fourth parameter determines what sort of access control is
   applied to the page. We want controlled access to these pages, so
   this parameter is set to PERMISSION

We’ll make it easier by extending the ``Grid3x3ViewBase`` base class from
the Krail core - this just gives us a 3x3 grid to place components in.



.. tip:: Extending ViewBase or one of its sub-classes is usually the easiest
    way to create your views, but however you do it, you must implement
    ``KrailView``. ``ViewBase`` can also help with
    `deserialization </userguide/userguide-serialisation.html>`__.

-  create the 3 views we want … AccountsView, FinanceView and
   PayrollView … just by extending ``Grid3x3ViewBase`` and injecting
   ``Translate`` (only FinanceView is shown here):

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    public class FinanceView extends Grid3x3ViewBase {

        @Inject
        protected FinanceView(Translate translate) {
            super(translate);
        }
    }

Defining the I18NKeys
---------------------

By default, if Krail’s ``I18NProcessor`` cannot find the value of an
``I18NKey``, it uses the name of the enum instead, with underscores
replaced with spaces. This means that as long as you are comfortable
with breaking the 'all-uppercase' convention for enum constant names,
you can get started quickly by not defining any values for the I18NKeys.
This is great for prototyping, and even if your application uses a
language with accents and diacriticals, the enum name may be good enough
for a prototype.

-  Add the required constants to LabelKey

.. code:: java

    package com.example.tutorial.i18n;

    import uk.q3c.krail.i18n.I18NKey;

    public enum LabelKey implements I18NKey {
        Accounts, Payroll, Finance
    }

Using the Pages
===============

Now we have defined the pages in a Guice module, we need to tell the
``BindingManager`` to include them:

.. code:: java

    @Override
       protected void addSitemapModules(List<Module> baseModules) {
           baseModules.add(new SystemAccountManagementPages());
           baseModules.add(new MyPages());
    }

View the Pages
==============

Run the application again. When the application starts the new pages
will not be visible - but that is what we should expect, as we said
these pages needed permission to view.

-  Log in (any username, password='password'), and you will see the
   pages, under 'Private', in the navigation tree and menu.

You may be wondering whether these pages need to be under the 'Private'
branch. At the moment they do, but only because of the very simple
access control rules supplied by ``DefaultRealm``. You can actually
define any logical structure, and we will see how to control permissions
in the `User Access Control <tutorial-uac.html>`__ section of the
Tutorial.

Add a Page - Annotation method
==============================

The second method of defining a page is to use an annotation on a
``KrailView`` implementation. To begin with, we need to tell Krail where
to look for annotated views - this reduces the amount of scanning Krail
has to do at start up. To do that we:

-  create a new class in the 'pages' package, "AnnotatedPagesModule" and
   extend ``AnnotationSitemapModule``

-  implement the ``define()`` method

-  add an entry in the define method, as below:

.. code:: java

    package com.example.tutorial.pages;

    import com.example.tutorial.i18n.LabelKey;
    import uk.q3c.krail.core.navigate.sitemap.AnnotationSitemapModule;

    public class AnnotatedPagesModule extends AnnotationSitemapModule {

        @Override
        protected void define() {
            addEntry("com.example.tutorial.pages",LabelKey.Accounts);
        }
    }

The call to ``addEntry`` tells Krail to recursively scan the
*com.example.tutorial.pages* package for classes with a
``@View annotation``. Multiple calls to ``addEntry`` can be made. The
second parameter should be an ``I18NKey`` from the same enum that you
are going to use in your ``@View`` annotations. The value you supply to
the ``addEntry`` method is just a sample, it just needs to be from the
same class. This is necessary because of the limitations on what Java
allows as ``Annotation`` parameter types

Now that this is done, any views in the 'pages' package, annotated with
``@View``, will be added to the Sitemap.

-  create another view, "PurchasingView" in the pages package,
   sub-classed from ``Grid3x3ViewBase``:

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.navigate.sitemap.View;
    import uk.q3c.krail.core.shiro.PageAccessControl;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    @View(uri = "private/finance/purchasing",pageAccessControl = PageAccessControl.PERMISSION,labelKeyName = "Purchasing")
    public class PurchasingView extends Grid3x3ViewBase {

        @Inject
        protected PurchasingView(Translate translate) {
            super(translate);
        }
    }

-  create the 'Purchasing' constant for ``LabelKey`` \` public enum
   LabelKey implements I18NKey { Accounts, Payroll, Finance, Purchasing
   } \`

-  tell the ``BindingManager`` to include the module we have just
   created

.. code:: java

     @Override
        protected void addSitemapModules(List<Module> baseModules) {
            baseModules.add(new SystemAccountManagementPages());
            baseModules.add(new MyPages());
            baseModules.add(new AnnotatedPagesModule());
        }

-  Run the application, log in and you will see that "Purchasing" has
   been added to the Finance page.

Choosing the Method
===================

You can mix Direct and Annotation sitemap entries however you wish, but
that can get a bit confusing to manage. Which method you choose is
mostly a matter of preference, but there is one feature of the direct
method you should be aware of.

Our direct pages module looks currently looks like this: \`
addEntry("private/finance", FinanceView.class, LabelKey.Finance,
PageAccessControl.PERMISSION); addEntry("private/finance/accounts",
AccountsView.class, LabelKey.Accounts, PageAccessControl.PERMISSION);
addEntry("private/finance/payroll", PayrollView.class, LabelKey.Payroll,
PageAccessControl.PERMISSION); \` There is a lot of repetition in the
URIs, so there is an alternative, by setting a ``rootURI`` which is
applied to all pages:

::

    package com.example.tutorial.pages;

    import com.example.tutorial.i18n.LabelKey;
    import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;
    import uk.q3c.krail.core.shiro.PageAccessControl;

    public class MyPages extends DirectSitemapModule {

    [source,java]
    ----
    public MyPages() {
        rootURI = "private/finance";
    }

    @Override
    protected void define() {
        addEntry("", FinanceView.class, LabelKey.Finance,
                PageAccessControl.PERMISSION);
        addEntry("accounts", AccountsView.class, LabelKey.Accounts,
                PageAccessControl.PERMISSION);
        addEntry("payroll", PayrollView.class, LabelKey.Payroll,
                PageAccessControl.PERMISSION);
    }
    ----

    }

-  update MyPages so it is as above

-  run the application and you will see that the pages appear in the
   same way as before

Moving a Set of Pages
=====================

We can easily move all the pages of a Direct module by changing the
``rootUri`` - they can be moved anywhere in the Sitemap, as a set, as
long the Sitemap maintains a logical structure. We will need to keep the
finance pages on the "Private" branch for now, because of the Access
Control rules, but as an example, let’s suppose we decide that it should
have a rootURI of "private/finance-department" instead:

-  modify the Binding Manager as below, to provide a different rootURI
   as the module is constructed:

.. code:: java

      @Override
        protected void addSitemapModules(List<Module> baseModules) {
            baseModules.add(new SystemAccountManagementPages());
            baseModules.add(new MyPages().rootURI("private/finance-department"));
            baseModules.add(new AnnotatedPagesModule());
        }

-  modify the annotated view (otherwise the Sitemap will break because
   there is no longer a "private/finance" page

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.navigate.sitemap.View;
    import uk.q3c.krail.core.shiro.PageAccessControl;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    @View(uri = "private/finance-department/purchasing",pageAccessControl = PageAccessControl.PERMISSION,labelKeyName = "Purchasing")
    public class PurchasingView extends Grid3x3ViewBase {

        @Inject
        protected PurchasingView(Translate translate) {
            super(translate);
        }
    }

-  Run the application and check that new URI is being used.

.. tip::    If you do want to set the rootURI directly in the module, you need
    to do so in the constructor, or it will prevent the fluent method
    shown above from working.


.. tip:: This feature of moving blocks of pages is available only with Direct pages. Although it might be possible to do something similar with annotated
    pages by mapping packages to URIs, there are currently no plans to
    do so.

Navigation
==========

Add some public pages
---------------------

Add a couple of public pages:

-  in the pages package create "MyPublicPages" class, extended from
   ``DirectSitemapModule`` with a couple of pages defined. Note that we
   are going to put these as 'roots' of the tree, as ``rootUri`` is set
   to an empty string.:

.. code:: java

    package com.example.tutorial.pages;

    import com.example.tutorial.i18n.LabelKey;
    import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;
    import uk.q3c.krail.core.shiro.PageAccessControl;

    public class MyPublicPages extends DirectSitemapModule {


    public MyPublicPages() {
        rootURI = "";
    }

    @Override
    protected void define() {
        addEntry("news", NewsView.class, LabelKey.News, PageAccessControl.PUBLIC);
        addEntry("contact-us", ContactUsView.class, LabelKey.Contact_Us, PageAccessControl.PUBLIC);

    }

-  Create the views, extended from\`Grid3x3ViewBase\`:

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    public class ContactUsView extends Grid3x3ViewBase {

    @Inject
    protected ContactUsView(Translate translate) {
        super(translate);
    }

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    public class NewsView extends Grid3x3ViewBase {


    @Inject
    protected NewsView(Translate translate) {
        super(translate);
    }

-  And add the ``LabelKey`` constants

.. code:: java

    public enum LabelKey implements I18NKey {
     Accounts, Payroll, Finance, News, Contact_Us, Purchasing
    }

-  Finally, update the ``BindingManager`` to include this new set of
   pages:

.. code:: java

     @Override
        protected void addSitemapModules(List<Module> baseModules) {
            baseModules.add(new SystemAccountManagementPages());
            baseModules.add(new MyPages().rootURI("private/finance-department"));
            baseModules.add(new AnnotatedPagesModule());
            baseModules.add(new MyPublicPages());
        }

Getting the Navigator
---------------------

We will do just a little bit more with these views to help demonstrate
navigation - we’ll just add some buttons to direct us to different URIs.
First, though, we need access to Krail’s ``Navigator``. We will inject
it into both views, using constructor injection:

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.navigate.Navigator;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    public class ContactUsView extends Grid3x3ViewBase {

    private Navigator navigator;

    @Inject
    protected ContactUsView(Translate translate, Navigator navigator) {
        super(translate);
        this.navigator = navigator;
    }

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.navigate.Navigator;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    public class NewsView extends Grid3x3ViewBase {

    private Navigator navigator;

    @Inject
    protected NewsView(Translate translate, Navigator navigator) {
        super(translate);
        this.navigator = navigator;
    }

Adding some components
----------------------

-  Add buttons and actions in the ``doBuild`` method of ``NewsView``:

.. code:: java

    @Override
     protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        Button navigateToContactUsBtn = new Button("Contact Us");
        Button navigateToPrivatePage = new Button("Accounts");
        navigateToContactUsBtn.addClickListener(c -&gt; navigator.navigateTo("contact-us"));
        navigateToPrivatePage.addClickListener(c-&gt;navigator.navigateTo("private/finance-department/accounts"));
        setCentreCell(new VerticalLayout(navigateToContactUsBtn,navigateToPrivatePage));
     }

The first two lines just create the buttons. The second two lines add
click listeners, which are set up to use the\`Navigator\`to direct us to
the chosen page. Then the buttons are added to a ``VerticalLayout`` which is put in the centre cell of the grid.

-  Run the application, **but do not log in.**

-  Click on the "News" page

-  Press the "Contact Us" button, and you will be taken to the "Contact
   Us" page

-  Press the browser back button, and you will be back on the "News"
   page

-  Press the "Accounts" button - and you a notification will appear to
   say that the page does not exist. As mentioned earlier, the same
   notification is given whether you are not authorised or the page does
   not exist.

-  Log in

-  Press the "Accounts" button again, and as you are now authorised, you
   will be at the "Accounts" page

Navigating with Parameters
--------------------------

A common requirement is to land on a page with parameters - a record id,
for example, so the page know which data to load. We are going to add a
"Contact Detail" page to simulate this.

-  Just as we’ve done before, add the page to '''MyPublicPages''',
   create the view and add the ``LabelKey`` constant:

.. code:: java

    package com.example.tutorial.pages;

    import com.example.tutorial.i18n.LabelKey;
    import uk.q3c.krail.core.navigate.sitemap.DirectSitemapModule;
    import uk.q3c.krail.core.shiro.PageAccessControl;

    public class MyPublicPages extends DirectSitemapModule {

    public MyPublicPages() {
        rootURI = "";
    }

    @Override
    protected void define() {
        addEntry("news", NewsView.class, LabelKey.News, PageAccessControl.PUBLIC);
        addEntry("contact-us", ContactUsView.class, LabelKey.Contact_Us, PageAccessControl.PUBLIC);
        addEntry("contact-us/contact-detail", ContactDetailView.class, LabelKey.Contact_Detail, PageAccessControl.PUBLIC);
    }

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import uk.q3c.krail.i18n.Translate;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;

    public class ContactDetailView extends Grid3x3ViewBase {

    @Inject
    protected ContactDetailView(Translate translate) {
        super(translate);
    }

Receiving parameters
~~~~~~~~~~~~~~~~~~~~

To set ``ContactDetailView`` up to receive parameters all we need to do
is override either the ``afterBuild`` method or the ``loadData`` method.
Using ``loadData`` (even if you are not loading data) means you won’t
forget to call **super.afterBuild()** first …​

.. code:: java

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import com.vaadin.ui.FormLayout;
    import com.vaadin.ui.Label;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;
    import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
    import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
    import uk.q3c.krail.i18n.Translate;

    public class ContactDetailView extends Grid3x3ViewBase {
     private Label idLabel;
     private Label nameLabel;

    @Inject
    protected ContactDetailView(Translate translate) {
        super(translate);
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        idLabel = new Label();
        idLabel.setCaption("id");
        nameLabel = new Label();
        nameLabel.setCaption("name");
        setCentreCell(new FormLayout(idLabel, nameLabel));
    }

    @Override
    protected void loadData(AfterViewChangeBusMessage busMessage) {
        idLabel.setValue(busMessage.getToState()
                .getParameterValue("id"));
        nameLabel.setValue(busMessage.getToState()
                .getParameterValue("name"));
    }

The process in ``loadData()`` is straightforward.  The busMessage is just an event, and it carries a reference to the navigation state we are navigating from, and the state we are navigating to.  This is represented by ``NavigationState``,
which also contains any parameters that have been passed with the URI.

Sending parameters
~~~~~~~~~~~~~~~~~~

To send parameters, construct a ``NavigationState``, specifying the
parameters to go with it and call ``Navigator.navigateTo(NavigationState)``

-  Update ``ContactUsView`` to add a button whose click listener builds
   the ``NavigationState``, adds parameters, then calls the
   ``Navigator``.

.. code:: java

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        Button navigateWithParametersBtn = new Button("Navigate with parameters");
        NavigationState navState = new NavigationState().virtualPage("contact-us/contact-detail")
                                                          .parameter("id", "33")
                                                      .parameter("name", "David");
        navigateWithParametersBtn.addClickListener(c->navigator.navigateTo(navState));
        setCentreCell(navigateWithParametersBtn);
    }

-  Run the application

-  select "Contact Us"

-  click on "Navigate with Parameters"

-  You will now be at the "Contact Detail" page with the parameter
   values displayed.

Excluding a page from Navigation
--------------------------------

If you think about the use of the "Contact Detail" page, it does not
actually make sense for it to appear in the navigation components - the
only time you would want to access this page is with some parameters to
set its contents:

-  Modify the page entry in ``MyPublicPages``, by setting the
   positionIndex parameter to < 0

.. code:: java

       @Override
    protected void define() {
              addEntry("news", NewsView.class, LabelKey.News, PageAccessControl.PUBLIC);
              addEntry("contact-us", ContactUsView.class, LabelKey.Contact_Us, PageAccessControl.PUBLIC);
              addEntry("contact-us/contact-detail", ContactDetailView.class, LabelKey.Contact_Detail, PageAccessControl.PUBLIC,-1);
    }

-  Run the application, and the page will no longer appear in the
   navigation components, but is actually still there:

   -  Go to the "Contact Us" page

   -  Press the "Navigate with Parameters" button

   -  The "Contact Detail" page appears as before.

Summary
=======

-  You have explored two methods of defining new pages, using Direct and
   Annotated methods.

-  You have created navigation actions from code

-  You have passed parameters to a page, as you typically might to load
   data

-  You have excluded a page from navigation, but still make it part of
   the Sitemap

-  You have "attached" an existing set of pages to a part of the Sitemap
   different from its default location

Download from GitHub
====================

To get to this point straight from GitHub:

.. code:: bash

    git clone https://github.com/davidsowerby/krail-tutorial.git
    cd krail-tutorial
    git checkout --track origin/krail_0.10.0.0

Revert to commit *Pages and Navigation Complete*
