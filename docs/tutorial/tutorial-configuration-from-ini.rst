============================
Configuration from INI files
============================

In the previous section we covered the use of options, and mentioned
that Krail sees Options as the `top layer of
configuration <../devguide/devguide-configuration-overview.md>`__.

This Tutorial has so far covered the top layer and part of the bottom
layer. The "bottom layer" includes the configuration we have done using
Guice and Guice modules, but includes anything which requires a
recompile (for example, annotations)

The middle layer is the one provided by the facility to load ini files
(and other formats), and that is what we will explore in this section

Overview
========

Krail integrates `Apache Commons
Configuration <https://commons.apache.org/proper/commons-configuration/>`__
to provide support for this form of loading configuration, which extends
well beyond just ini files. (See the Apache documentation for more
information).

More specifically, Krail captures configuration information in an
instance of ``ApplicationConfiguration``, which allows a set of
configuration values to override a previous set (when they have the same
property names). This is similar in principle to the way
`options <tutorial-options.md>`__ work.

Example
=======

-  In the 'pages' package create a new view, 'IniConfigView', extended
   from ``Grid3x3ViewBase``

-  Override the ``doBuild()`` method (you will get some compile errors)

::

    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import com.vaadin.ui.Alignment;
    import com.vaadin.ui.Button;
    import com.vaadin.ui.Label;
    import uk.q3c.krail.config.ApplicationConfiguration;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;
    import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
    import uk.q3c.krail.i18n.Translate;

    public class IniConfigView extends Grid3x3ViewBase {

    [source]
    ----
    private final ApplicationConfiguration applicationConfiguration;
    private Label tutorialQualityProperty;
    private Label connectionTimeoutProperty;
    private Label tutorialCompletedProperty;

    @Inject
    protected IniConfigView(Translate translate, ApplicationConfiguration applicationConfiguration) {
        super(translate);
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        Button showConfigButton = new Button("Show config");
        tutorialQualityProperty = new Label();
        showConfigButton.addClickListener(event -> showConfig());
        setTopCentre(tutorialQualityProperty);
        setMiddleCentre(showConfigButton);
        getGridLayout().setComponentAlignment(tutorialQualityProperty, Alignment.MIDDLE_CENTER);
        connectionTimeoutProperty = new Label();
        tutorialCompletedProperty = new Label();
        setTopRight(tutorialCompletedProperty);
        setTopLeft(connectionTimeoutProperty);

    }
    private void showConfig() {
        tutorialQualityProperty.setValue("Tutorial quality is: " + applicationConfiguration.getString("tutorial.quality"));
        tutorialCompletedProperty.setValue("Tutorial completed: "+ applicationConfiguration.getString("tutorial.completed"));
        connectionTimeoutProperty.setValue("The timeout is set to: " + applicationConfiguration.getString("connection.timeout"));
    }
    ----

    }
    `
    This sets up a button to show the config, and labels to display the values.  Loading the config into a singleton instance of`ApplicationConfiguration``` actually happens at application startup.

    We can inject `ApplicationConfiguration` anywhere in the application to gain access to the configuration data loaded from the ini files (or any of the many other sources https://commons.apache.org/proper/commons-configuration/[Apache Commons Configuration] supports)

    Note the `showConfig()` method could equally be placed directly in the lambda expression for `showConfigButton`

    * Include this new page in `MyOtherPages`
    [source]
    ----
    addEntry("ini-config", IniConfigView.class, LabelKey.Ini_Config, PageAccessControl.PUBLIC);
    ----

    = More layers

    When an application comprises multiple libraries, there may be a need for multiple sets of configuration. You can add as many configuration files as you require.

    == Adding ini files

    * create a file 'krail.ini' in _src/main/webapp/WEB-INF_
    * you may be familiar with the extended properties file format …. populate it with:
    `
    [tutorial]
    quality=good
    completed=false
    `
    *

    create another file 'moreConfig.ini' in WEB-INF, with this content:

quality=brilliant

timeout=1000

::

    This will be used to show a property overriding another, while also adding new properties.

    == Configure Guice

    We now need to set up the Guice configuration so it knows about the additional file. You can sub-class `ApplicationConfigurationModule` , and then tell the `BindingManager` about it, or more easily, simply add the configs as part of the the `BindingManager` entry like this:

    [source]
    ----
        @Override
        protected Module applicationConfigurationModule() {
            return new KrailApplicationConfigurationModule().addConfig("moreConfig.ini",98,false).addConfig("krail.ini",100,true);
        }
    ----

    Be aware that the order that the files are processed is important if they contain the same (fully qualified) property names. If you look at the javadoc for `addConfig()` you will see that the second parameter determines the order (priority) of loading, with a lower value being the highest priority (0 is therefore the highest priority)

    * Run the application and select the "Ini Config" page
    *

    Press "Show config" and you will see the values provided by _krail.ini_ and _moreConfig.ini_ combined:

    ** _tutorial.completed_ from _krail.ini_ is unchanged as there is no value for it in _moreConfig.ini_
    ** _connection.timeout_ is a new property from _moreConfig.ini_
    ** _tutorial.quality_ from _krail.ini_ has been overridden by the value in _moreConfig.ini_

    = Fail early

    If an ini file is essential for the operation of your application, `addConfig()` allows you to specify that. Both the examples have the 'optional' parameter set to 'false', but of course both files are present.

    * add another config to the `BindingManager entry`, but do not create the corresponding file
    `
    @Override
    protected Module applicationConfigurationModule() {
        return new KrailApplicationConfigurationModule()
                .addConfig(&quot;moreConfig.ini&quot;,98,false)
                .addConfig(&quot;essential.ini&quot;,99,false)
                .addConfig(&quot;krail.ini&quot;,100,true);
    }
    `
    * run the application and it will fail early with a `FileNotFoundException` (Note: there is currently a https://github.com/davidsowerby/krail/issues/531[bug] which causes a timeout rather than an exception)
    * change the 'optional' parameter to true and the application will run
    [source]
    ----
        @Override
        protected Module applicationConfigurationModule() {
            return new KrailApplicationConfigurationModule()
                    .addConfig("moreConfig.ini",98,false)
                    .addConfig("essential.ini",99,false)
                    .addConfig("krail.ini",100,false);
        }
    ----

    The final versions of the files should be:

    [source]
    ----
    package com.example.tutorial.app;

    import com.example.tutorial.i18n.LabelKey;
    import com.example.tutorial.pages.AnnotatedPagesModule;
    import com.example.tutorial.pages.MyOtherPages;
    import com.example.tutorial.pages.MyPages;
    import com.example.tutorial.pages.MyPublicPages;
    import com.google.inject.Module;
    import uk.q3c.krail.core.config.KrailApplicationConfigurationModule;
    import uk.q3c.krail.core.guice.DefaultServletContextListener;
    import uk.q3c.krail.core.navigate.sitemap.SystemAccountManagementPages;
    import uk.q3c.krail.core.sysadmin.SystemAdminPages;
    import uk.q3c.krail.core.ui.DefaultUIModule;

    import java.util.List;

    public class BindingManager extends DefaultBindingManager {

        @Override
        protected Module servletModule() {
            return new TutorialServletModule();
        }

        @Override
        protected void addAppModules(List<Module> modules) {

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
            return new DefaultUIModule().uiClass(TutorialUI.class).applicationTitleKey(LabelKey.Krail_Tutorial);
        }

        @Override
        protected Module applicationConfigurationModule() {
            return new KrailApplicationConfigurationModule()
                    .addConfig("moreConfig.ini",98,false)
                    .addConfig("essential.ini",99,true)
                    .addConfig("krail.ini",100,true);
        }
    }

    ----

    [source]
    ----
    package com.example.tutorial.pages;

    import com.google.inject.Inject;
    import com.vaadin.ui.Alignment;
    import com.vaadin.ui.Button;
    import com.vaadin.ui.Label;
    import uk.q3c.krail.config.ApplicationConfiguration;
    import uk.q3c.krail.core.view.Grid3x3ViewBase;
    import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
    import uk.q3c.krail.i18n.Translate;

    public class IniConfigView extends Grid3x3ViewBase {

        private final ApplicationConfiguration applicationConfiguration;
        private Label tutorialQualityProperty;
        private Label connectionTimeoutProperty;
        private Label tutorialCompletedProperty;

        @Inject
        protected IniConfigView(Translate translate, ApplicationConfiguration applicationConfiguration) {
            super(translate);
            this.applicationConfiguration = applicationConfiguration;
        }

        @Override
        protected void doBuild(ViewChangeBusMessage busMessage) {
            super.doBuild(busMessage);
            Button showConfigButton = new Button("Show config");
            tutorialQualityProperty = new Label();
            showConfigButton.addClickListener(event -> showConfig());
            setTopCentre(tutorialQualityProperty);
            setMiddleCentre(showConfigButton);
            getGridLayout().setComponentAlignment(tutorialQualityProperty, Alignment.MIDDLE_CENTER);
            connectionTimeoutProperty = new Label();
            tutorialCompletedProperty = new Label();
            setTopRight(tutorialCompletedProperty);
            setTopLeft(connectionTimeoutProperty);

        }
        private void showConfig() {
            tutorialQualityProperty.setValue("Tutorial quality is: " + applicationConfiguration.getString("tutorial.quality"));
            tutorialCompletedProperty.setValue("Tutorial completed: "+ applicationConfiguration.getString("tutorial.completed"));
            connectionTimeoutProperty.setValue("The timeout is set to: " + applicationConfiguration.getString("connection.timeout"));
        }
    }
    ----

    = Summary

    * We have loaded an ini file
    * we have demonstrated the principle of overriding the the values in one ini file with those from another
    * We have demonstrated ensuring an early fail if a file is missing
    * We have demonstrate making the presence of an ini file optional

    Apache Commons Configuration supports much more than just ini files, and can support https://commons.apache.org/proper/commons-configuration/userguide_v1.10/overview.html#Configuration_Sources[variety of sources] - Krail will just accept anything that Apache Commons Configuration provides

    = Download from GitHub

    To get to this point straight from GitHub:

    [source,bash]
    ----
    git clone https://github.com/davidsowerby/krail-tutorial.git
    cd krail-tutorial
    git checkout --track origin/krail_0.10.0.0

    ----

    Revert to commit _Configuration from ini file complete_
