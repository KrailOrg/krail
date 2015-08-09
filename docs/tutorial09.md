#Introduction

JPA support for Krail is provided by the [krail-jpa](https://github.com/davidsowerby/krail-jpa) library, which in turn is mostly provided by [Apache Onami Persist](https://onami.apache.org/persist/).  This was chosen in preference to [guice-persist](https://github.com/google/guice/wiki/GuicePersist), primarily for its ability to support multiple concurrent database instances.  

A useful comparison  of Onami Persist and Guice Persist can be found [here](https://onami.apache.org/persist/guicePersist.html).

Krail assumes that one day you will want to use multiple persistence units - that may not be the case, but makes it easier if it is required. All this requires is to use an annotation to identify a persistence source, and it gives you an element of standardisation and future-proofing.

A generic Dao is provided (primarily for use in lambdas, but also there if that is just the way you prefer to work).  Implementations are also provided for the Krail core - for ```OptionDao``` and I18N ```PatternDao```

A reasonable understanding of JPA is assumed.

#Example

*We will*
 
- create a page, 
- configure two database connections (one HSQLDB and One Apache Derby), 
- demonstrate some simple transactions
- demonstrate the use of JPAContainer to provide Tables, with two databases, 
- demonstrate integration with Krail I18N and ```Option```

*We will not:*

- Attempt to demonstrate all of the standard JPA capability - for that a JPA tutorial would be more appropriate 
 
#Prepare build

- include krail-jpa in the build, by adding it to *build.gradle* dependencies
- remove the javax persistence dependency from the earlier Tutorial steps, as it is provided as part of krail-jpa 
```groovy
dependencies {
    compile 'uk.q3c.krail:krail:0.9.6'
    compile 'uk.q3c.krail:krail-jpa:0.9.0'
}
```
 
#Create a Page

If you have followed the whole Tutorial, you will be an expert at this by now
 
- add a public page to ```MyOtherPages```

```
addEntry("jpa", JpaView.class, LabelKey.JPA, PageAccessControl.PUBLIC);
```
- create JpaView in package 'com.example.tutorial.pages', extended from ```ViewBase```

```
package com.example.tutorial.pages;

import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class JpaView extends ViewBase {
    
    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        
    }
}
```

- add the constant 'JPA' to ```LabelKey```

#Configure connections

This is one occasion where it is more desirable to sub-class the relevant Guice module than use fluent methods. There is a lot that can be configured for a database instance, so configuration objects are used.

- create a new package 'com.example.tutorial.jpa'
- in this package create 'TutorialJpaModule' extended from JpaModule

```
package com.example.tutorial.jpa;

import uk.q3c.krail.jpa.persist.JpaModule;

public class TutorialJpaModule extends JpaModule {

    @Override
    protected void define() {

    }
}
```
- add two persistence units in the ```define()``` method
```
@Override
protected void define() {
    addPersistenceUnit("derbyDb", DerbyJpa.class, derbyConfig());
    addPersistenceUnit("hsqlDb", HsqlJpa.class, hsqlConfig());
}
```
- create the 'DerbyJpa' and 'HsqlJpa' annotations - these are Guice Binding Annotations (denoted by **@BindingAnnotation**), which will enable you to select which persistence unit you want to use from within the application.

```
package com.example.tutorial.jpa;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface DerbyJpa {
}
```
```
package com.example.tutorial.jpa;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface HsqlJpa {
}
```
- create a temporary folder for our Derby database.  For this Tutorial we will just use the module constructor, though this is not a recommended approach for production!

```
public class TutorialJpaModule extends JpaModule {
    File userHome = new File(System.getProperty("user.home"));
    File tempDir = new File(userHome, "temp/krail-tutorial");

    public TutorialJpaModule() {
        
        try {
            FileUtils.forceMkdir(tempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

- Provide a configuration object for each connection, using the ```derbyConfig()``` and  ```hsqlConfig()``` methods. These are standard JPA configuration settings composed into a configuration object:

```
private DefaultJpaInstanceConfiguration derbyConfig() {
    DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
    File dbFolder = new File(tempDir, "derbyDb");

    config.transactionType(DefaultJpaInstanceConfiguration.TransactionType.RESOURCE_LOCAL)
          .db(JpaDb.DERBY_EMBEDDED)
          .autoCreate(true)
          .url(dbFolder.getAbsolutePath())
          .user("test")
          .password("test")
          .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);
    return config;
}
```


```
private DefaultJpaInstanceConfiguration hsqlConfig() {
    DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
    config.db(JpaDb.HSQLDB)
          .autoCreate(true)
          .url("mem:test")
          .user("sa")
          .password("")
          .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);
    return config;
}
```
- update the ```BindingManager``` to make it aware of this new module.  This would override the use of the default ```InMemoryModule```, but we want that as well for demonstration purposes
```
@Override
protected void addPersistenceModules(List<Module> modules) {
    super.addPersistenceModules(modules);
    modules.add(new TutorialJpaModule());
}
```


- Unfortunately we still need a minimal persistence.xml file, so we need to
       - create folder src/main/resources/META-INF
       - create the following *persistence.xml* file in that folder

```
<?xml version="1.0" encoding="UTF-8" ?>
<persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0" xmlns="http://java.sun.com/xml/ns/persistence">
    <persistence-unit name="derbyDb">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>
        <properties>
        </properties>

    </persistence-unit>

    <persistence-unit name="hsqlDb">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>
        <properties>
        </properties>

    </persistence-unit>
</persistence>
```

#Prepare the service

- configure the ```TutorialServletModule``` to add the ```PersistenceFilter```

```
package com.example.tutorial.app;

import org.apache.onami.persist.PersistenceFilter;
import uk.q3c.krail.core.guice.BaseServletModule;

public class TutorialServletModule extends BaseServletModule {

    @Override
    protected void configureServlets() {
        filter("/*").through(PersistenceFilter.class);
        serve("/*").with(TutorialServlet.class);
    }
}
```

#Prepare the Entity

- Update the ```Person``` entity we used earlier, to be JPA compliant
    - add the **@Entity** class annotation
    - use auto-generated id
    
```
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```


#Prepare the user interface

- set up the basic layout components
```
@Override
protected void doBuild(ViewChangeBusMessage busMessage) {
    Panel panel = new Panel();
    setRootComponent(panel);
}
```

In ```JpaView``` we want to show a table each for the Derby and HSQLDB connections. A Vaadin ```Table``` uses a ```Container``` to provide the data, and in this case a ```JPAContainer```.  

- To get a container, we need to inject a ```JpaContainerProvider``` for each persistence unit, identified by their annotations, **@DerbyJpa** and **@HsqlJpa**

```
package com.example.tutorial.pages;

import com.example.tutorial.jpa.DerbyJpa;
import com.example.tutorial.jpa.HsqlJpa;
import com.google.inject.Inject;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.jpa.persist.JpaContainerProvider;

public class JpaView extends ViewBase {

    private final JpaContainerProvider derbyContainerProvider;
    private final JpaContainerProvider hsqlContainerProvider;

    @Inject
    protected JpaView(@DerbyJpa JpaContainerProvider derbyContainerProvider, @HsqlJpa JpaContainerProvider hsqlContainerProvider) {
        this.derbyContainerProvider = derbyContainerProvider;
        this.hsqlContainerProvider = hsqlContainerProvider;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {

    }
}
```
- complete the layout so it looks like this

```
@Override
protected void doBuild(ViewChangeBusMessage busMessage) {
    derbyContainer=derbyContainerProvider.get(Person.class, ContainerType.CACHED);
    hsqlContainer=hsqlContainerProvider.get( Person.class, ContainerType.CACHED);
    derbyTable = new Table("",derbyContainer);
    hsqlTable = new Table("",hsqlContainer);

    VerticalLayout derbyLayout = new VerticalLayout(derbyTable);
    VerticalLayout hsqlLayout = new VerticalLayout(hsqlTable);

    HorizontalLayout horizontalLayout=new HorizontalLayout(derbyLayout,hsqlLayout);
    Panel panel = new Panel();
    panel.setContent(horizontalLayout);
    setRootComponent(panel);
}
```

The Vaadin ```Table```s, are using containers from the ```JpaContainerProvider```s to provide the data


- Now we need to provide the I18N captions for the ```Table``` components


```
@Caption(caption = LabelKey.Derby_Table, description = DescriptionKey.Table_connected_to_DerbyDb)
private Table derbyTable;
@Caption(caption = LabelKey.HSQL_Table, description = DescriptionKey.Table_connected_to_HsqlDb)
private Table hsqlTable;
```
- run the application just to make sure you have everything correctly set up so far.  There is no data to display yet, so all you will see is two empty tables.

#Data

- in ```JPAView```, create a convenience method for creating new people
```
private Person createPerson() {
    Person p = new Person();
    int i=new Random().nextInt(5000);
    p.setAge(i % 80);
    p.setFirstName("First name "+i);
    p.setLastName("Last name " + i);
    return p;
}
```

There are different ways of accessing the data.

##Using the EntityManager

This is the method recommended by the Apache Onami team:

- inject an ```EntityManagerProvider``` (The Onami provider, not the Vaadin provider) for each persistence unit, using the binding annotations to identify them

```
@Inject
protected JpaView(@DerbyJpa JpaContainerProvider derbyContainerProvider, @HsqlJpa JpaContainerProvider hsqlContainerProvider, @DerbyJpa EntityManagerProvider derbyEntityManagerProvider, @HsqlJpa EntityManagerProvider hsqlEntityManagerProvider) {
    this.derbyContainerProvider = derbyContainerProvider;
    this.hsqlContainerProvider = hsqlContainerProvider;
    this.derbyEntityManagerProvider = derbyEntityManagerProvider;
    this.hsqlEntityManagerProvider = hsqlEntityManagerProvider;
}
```
- create a method to undertake the transaction
 
```
@Transactional
protected void addWithEntityMgr(EntityManagerProvider entityManagerProvider) {
    final EntityManager entityManager = entityManagerProvider.get();
    entityManager.persist(createPerson());
}
```
- add two buttons to call the ```addWithEntityMgr``` method, and refresh the container (so that we can see the changes)
- add the buttons to the vertical layouts

```
//add with EntityManager
derbyEntityMgrButton = new Button();
derbyEntityMgrButton.addClickListener(event -> {
    addWithEntityMgr(derbyEntityManagerProvider);
    derbyContainer.refresh();
});
hsqlEntityMgrButton = new Button();
hsqlEntityMgrButton.addClickListener(event -> {
    addWithEntityMgr(hsqlEntityManagerProvider);
    hsqlContainer.refresh();
});

VerticalLayout derbyLayout = new VerticalLayout(derbyTable, derbyEntityMgrButton);
VerticalLayout hsqlLayout = new VerticalLayout(hsqlTable, hsqlEntityMgrButton);

```
- give the buttons captions and descriptions

```
@Caption(caption = LabelKey.Add_with_entity_manager, description = DescriptionKey.Add_with_entity_manager)
private Button derbyEntityMgrButton;
@Caption(caption = LabelKey.Add_with_entity_manager, description = DescriptionKey.Add_with_entity_manager)
private Button hsqlEntityMgrButton;
```
- run the application and press the buttons
    - you will see that each persistence unit is operating separately, just by use of the binding annotations
     
<div class="warning">
<p class="first admonition-title">Warning</p>
<p class="last">When you use the IDE to create methods they generally default to being <b>private</b>.  BUT Guice AOP does not work on private, static or final methods - so that is the first thing to check if your <b>@Transactional</b> method does not work.</p>
</div>

##DAO

Of course, it is not possible to annotate a lambda, so Krail provides a generic DAO for the simple JPA calls to avoid the need for creating annotated methods.

- inject the DAO for each persistence unit

```
@Inject
protected JpaView(@DerbyJpa JpaContainerProvider derbyContainerProvider, @HsqlJpa JpaContainerProvider hsqlContainerProvider, @DerbyJpa EntityManagerProvider derbyEntityManagerProvider, @HsqlJpa EntityManagerProvider hsqlEntityManagerProvider, @DerbyJpa JpaDao_LongInt derbyDao, @HsqlJpa JpaDao_LongInt hsqlDao) {
    this.derbyContainerProvider = derbyContainerProvider;
    this.hsqlContainerProvider = hsqlContainerProvider;
    this.derbyEntityManagerProvider = derbyEntityManagerProvider;
    this.hsqlEntityManagerProvider = hsqlEntityManagerProvider;
    this.derbyDao = derbyDao;
    this.hsqlDao = hsqlDao;
}
```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">For type safety, Krail's Dao and Entity require parameters to identify the type of id and version used.  <code>JpaDao_LongInt</code> simply denotes that it is a JPA DAO using Long for the Id and Integer for the version - the correct types for our Person class</p>
</div>

- DAOs are not bound automatically, so we add them to the persistence unit configuration in ```TutorialJpaModule``` by calling ```useLongIntDao()``` on the ```JpaInstanceConfiguration``` (add to HSQL configuration as well, even though only Derby configuration is shown here)

```
private DefaultJpaInstanceConfiguration derbyConfig() {
    DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
    File dbFolder = new File(tempDir, "derbyDb");

    config.transactionType(DefaultJpaInstanceConfiguration.TransactionType.RESOURCE_LOCAL)
          .db(JpaDb.DERBY_EMBEDDED)
          .autoCreate(true)
          .url(dbFolder.getAbsolutePath())
          .user("test")
          .password("test")
          .useLongIntDao()
          .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);
    return config;
}

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last"><code>useLongIntDao()</code> is shorthand for calling:<br><code>    JpaInstanceConfiguration.bind(JpaDaoLongInt.class, DefaultJpaDaoLongInt.class)</code>.<br> You can use <code>bind()</code> to bind any persistence unit specific implementation so that you can identify them with binding annotations - for example, to inject your own Dao implementation:<br> <code>@DerbyJpa MyDao myDao</code></p>
</div>

    
    
```
- add buttons to ```JpaView.doBuild()```  

```
//add with Dao
derbyDaoButton = new Button();
derbyDaoButton.addClickListener(event -> {
    derbyDao.save(createPerson());
    derbyContainer.refresh();
});
hsqlDaoButton = new Button();
hsqlDaoButton.addClickListener(event -> {
    hsqlDao.save(createPerson());
    hsqlContainer.refresh();
});


```
- include them in the layout
```
VerticalLayout derbyLayout = new VerticalLayout(derbyTable, derbyEntityMgrButton, derbyDaoButton);
VerticalLayout hsqlLayout = new VerticalLayout(hsqlTable, hsqlEntityMgrButton, hsqlDaoButton);
```
- give them I18N captions and descriptions

```
@Caption(caption = LabelKey.Add_with_DAO, description = DescriptionKey.Add_with_DAO)
private Button derbyDaoButton;
@Caption(caption = LabelKey.Add_with_DAO, description = DescriptionKey.Add_with_DAO)
private Button hsqlDaoButton;
```
- run the application, navigate to JPA
    - the "add with DAO" buttons work in the same way as the "add with EntityManager" buttons


<a name="persistence-option"></a>
#Persistence for Option

```Option``` values are saved to the **@InMemory** store by default, which is not very useful unless you have an "always on" system.

First we will demonstrate that ```Option``` values are saved to the to the **@InMemory** store, and then we will change settings to demonstrate them being saved to a JPA PU instead.

We need a new page:

- add a new page to ```MyOtherPages``` as a sub page of the "JPA" page:

```
addEntry("jpa/option", JpaOptionView.class, LabelKey.Options, PageAccessControl.PUBLIC);
```
- create a new class JpaOptionView in the 'pages' package

```
package com.example.tutorial.pages;

import com.example.tutorial.i18n.Caption;
import com.example.tutorial.i18n.DescriptionKey;
import com.example.tutorial.i18n.LabelKey;
import com.example.tutorial.jpa.DerbyJpa;
import com.google.inject.Inject;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import uk.q3c.krail.core.persist.ContainerType;
import uk.q3c.krail.core.persist.InMemoryContainer;
import uk.q3c.krail.core.persist.VaadinContainerProvider;
import uk.q3c.krail.core.user.opt.*;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.jpa.persist.JpaContainerProvider;
import uk.q3c.krail.jpa.user.opt.OptionEntity_LongInt;

import javax.annotation.Nonnull;

public class JpaOptionView extends ViewBase implements OptionContext {


    public static final OptionKey<String> anyOldText = new OptionKey<>("default text", MyNews.class, LabelKey.Age, DescriptionKey.Age_of_the_Person);
    private final VaadinContainerProvider inMemoryContainerProvider;
    private final JpaContainerProvider derbyContainerProvider;
    private JPAContainer<OptionEntity_LongInt> derbyContainer;
    private InMemoryContainer inMemoryContainer;

    @Caption(caption = LabelKey.In_Memory, description = DescriptionKey.Interesting_Things )
    private Table inMemoryTable;
    @Caption(caption = LabelKey.Derby, description = DescriptionKey.Interesting_Things )
    private Table derbyTable;
    private Option option;
    private OptionPopup optionPopup;

    @Caption(caption = LabelKey.Options, description = DescriptionKey.Interesting_Things )
    private Button optionPopupButton;

    @Inject
    protected JpaOptionView(@InMemory VaadinContainerProvider inMemoryContainerProvider, @DerbyJpa JpaContainerProvider derbyContainerProvider, OptionPopup
            optionPopup, Option option) {
        this.inMemoryContainerProvider = inMemoryContainerProvider;
        this.derbyContainerProvider = derbyContainerProvider;
        this.optionPopup = optionPopup;
        this.option = option;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        optionPopupButton = new Button();
        optionPopupButton.addClickListener(event -> optionPopup.popup(this, LabelKey.Options));
        inMemoryTable = new Table();
        derbyTable = new Table();
        inMemoryContainer = (InMemoryContainer) inMemoryContainerProvider.get(OptionEntity.class, ContainerType.CACHED);
        derbyContainer = derbyContainerProvider.get(OptionEntity_LongInt.class, ContainerType.CACHED);
        inMemoryTable.setContainerDataSource(inMemoryContainer);
        derbyTable.setContainerDataSource(derbyContainer);

        HorizontalLayout horizontalLayout = new HorizontalLayout(optionPopupButton, inMemoryTable, derbyTable);
        setRootComponent(new Panel(horizontalLayout));

    }

    @Nonnull
    @Override
    public Option getOption() {
        return option;
    }

    @Override
    public void optionValueChanged(Property.ValueChangeEvent event) {
        inMemoryContainer.refresh();
        derbyContainer.refresh();
    }
}

```

There is quite a lot in this class, but you have seen most of it already - these are the key points:

- an ```OptionKey``` is defined purely for demonstrating a change of value
- We are injecting ContainerProviders to provide Vaadin Container instances fro the Vaadin Tables
- A Vaadin ```Table``` is used for each persistence source to present the data
- the ```OptionPopup``` is used so that we can change the value of an ```Option```
- the ```optionValueChanged()``` method refreshes the both ```Container``` (and associated ```Table```) instances when an ```Option``` value is changed

Now to check what is happening:

- run the application and log in (for example 'eq'/'eq') so that you can change the option value
- navigate to "JPA | Options"
- click the "Options" button and change the option value
- the "In Memory" table will update

##Changing to JPA

- configure the JPA provider to bind an ```OptionDao```.  This is done by amending the config in ```TutorialJpaModule``` to include a call to ```provideOptionDao()```:

```
   private DefaultJpaInstanceConfiguration derbyConfig() {
        DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
        File dbFolder = new File(tempDir, "derbyDb");

        config.transactionType(DefaultJpaInstanceConfiguration.TransactionType.RESOURCE_LOCAL)
              .db(JpaDb.DERBY_EMBEDDED)
              .autoCreate(true)
              .url(dbFolder.getAbsolutePath())
              .user("test")
              .useLongIntDao()
              .provideOptionDao()
              .password("test")
              .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);
        return config;
    }
```
- select **@DerbyJpa** as the active source for ```Option``` by modifying the ```BindingManager```:
 
```
@Override
protected Module optionModule() {
    return new OptionModule().activeSource(DerbyJpa.class);
}
```

- add the JPA Option entity to *persistence.xml*
 
```xml
<persistence-unit name="derbyDb">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>uk.q3c.krail.jpa.user.opt.OptionEntity_LongInt</class>
    <exclude-unlisted-classes>false</exclude-unlisted-classes>
    <properties>
    </properties>

</persistence-unit>
```

- Run the application and log in
- navigate to "JPA | Options"
- click the "Options" button and change the option value
- the "Derby" table will update instead of the "In Memory" table 


<a name="persistence-i18n"></a>
#Persistence for I18N

Persistence for I18N patterns is a little different to persistence for ```Option```.  For ```Option```, there is only ever one source in use, but as we have already seen, we can use multiple sources for I18N patterns, working in a hierarchy.

To demonstrate this we will go back to the JPA page - and if you wish to check first, you will see that none of the Tutorial display for this page is translated.  We will add a translation to the Derby source, update the configuration and see the translation take effect.

- add the **@DerbyJpa** pattern dao to the constructor injections 
```
@Inject
protected JpaView(@DerbyJpa JpaContainerProvider derbyContainerProvider, @HsqlJpa JpaContainerProvider hsqlContainerProvider, @DerbyJpa EntityManagerProvider derbyEntityManagerProvider, @HsqlJpa EntityManagerProvider hsqlEntityManagerProvider, @DerbyJpa JpaDao_LongInt derbyDao, @HsqlJpa JpaDao_LongInt hsqlDao, @DerbyJpa PatternDao patternDao) {
    this.derbyContainerProvider = derbyContainerProvider;
    this.hsqlContainerProvider = hsqlContainerProvider;
    this.derbyEntityManagerProvider = derbyEntityManagerProvider;
    this.hsqlEntityManagerProvider = hsqlEntityManagerProvider;
    this.derbyDao = derbyDao;
    this.hsqlDao = hsqlDao;
    this.patternDao = patternDao;
}
```
- create a button to insert a new value into the Derby pattern table:

```
derbyPatternButton = new Button();
derbyPatternButton.addClickListener(event->{patternDao.write(new PatternCacheKey(LabelKey.Derby_Table, Locale.GERMANY),"Tafel aus Derby");});

VerticalLayout derbyLayout = new VerticalLayout(derbyTable, derbyEntityMgrButton, derbyDaoButton,derbyPatternButton);

```
- provide a caption and description

```
@Caption(caption = LabelKey.Insert_Pattern_value, description = DescriptionKey.Insert_Pattern_value)
private Button derbyPatternButton;
```
- In the same way as we did for ```Option```, set up the Derby configuration in ```TutorialJpaModule``` to produce a pattern dao by a call to ```providePatterDao()```

```
private DefaultJpaInstanceConfiguration derbyConfig() {
    DefaultJpaInstanceConfiguration config = new DefaultJpaInstanceConfiguration();
    File dbFolder = new File(tempDir, "derbyDb");

    config.transactionType(DefaultJpaInstanceConfiguration.TransactionType.RESOURCE_LOCAL)
          .db(JpaDb.DERBY_EMBEDDED)
          .autoCreate(true)
          .url(dbFolder.getAbsolutePath())
          .user("test")
          .useLongIntDao()
          .provideOptionDao()
          .providePatternDao()
          .password("test")
          .ddlGeneration(DefaultJpaInstanceConfiguration.Ddl.DROP_AND_CREATE);
    return config;
}
```
- instruct the I18NModule to use **@DerbyJpa** as a source - we will put it in first place to ensure that it is picked up first - but we still want to use the Class based definitions if there is nothing in the Derby source:

```
@Override
protected Module i18NModule() {
    return new TutorialI18NModule().source(DerbyJpa.class)
                                   .source(ClassPatternSource.class);
}
```
- add the JPA pattern entity to *persistence.xml*

```xml
<persistence-unit name="derbyDb">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>uk.q3c.krail.jpa.user.opt.OptionEntity_LongInt</class>
    <class>uk.q3c.krail.jpa.i18n.PatternEntity_LongInt</class>
    <exclude-unlisted-classes>false</exclude-unlisted-classes>
    <properties>
    </properties>

</persistence-unit>
```

- run the application and navigate to "JPA"
- press the "Insert Pattern Value" button to save a translation for "Derby Table" into the **@DerbyJpa** PU
- use the Locale selector to change to "Deutsch"
- The caption for the Derby table now shows the German translation

#Summary

We have :

- configured two database connections (one HSQLDB and One Apache Derby),
- kept the In Memory source, working in conjunction with JPA sources
- demonstrated some simple transactions using method annotation
- demonstrated transactions from within a lambda
- used the generic DAO for both JPA sources
- used JPA containers, with sources identified by annotation
- configured ```Option``` to use JPA persistence  
- configured I18N to use JPA for pattern persistence

#Download from GitHub
To get to this point straight from GitHub, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step09**