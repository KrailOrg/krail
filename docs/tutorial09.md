#Introduction

JPA support for Krail is provided by the [krail-jpa](https://github.com/davidsowerby/krail-jpa) library, which in turn is mostly provided by [Apache Onami Persist](https://onami.apache.org/persist/).  This was chosen in preference to [guice-persist](https://github.com/google/guice/wiki/GuicePersist), primarily for its ability to support multiple concurrent database instances.  

A useful comparison  of Onami Persist and Guice Persist can be found [here](https://onami.apache.org/persist/guicePersist.html).

In this section we will assume that you want to use multiple persistence units - even if you do not, we would recommend starting with the assumption that you will one day.  There is minimal extra effort to do so (just the use of an annotation), and it gives you an element of standardisation and future-proofing.

A generic Dao is provided (primarily for use in lambdas, but also there if that is just the way you prefer to work).  Dao implementations for krail core (for Option and I18N Patterns)

A reasonable understanding of JPA is assumed.

#Example

*We will*
 
- create a page, 
- configure two database connections (one HSQLDB and One Apache Derby), 
- demonstrate some simple transactions
- demonstrate the use of JPAContainer with two databases, to provide Tables
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

import uk.q3c.krail.persist.jpa.JpaModule;

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
- update the ```BindingManager``` to make it aware of this new module.  This will override the use of the default ```InMemoryModule```
```
@Override
protected void addPersistenceModules(List<Module> modules) {
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

- To get a container, we need to inject a ```JpaContainerProvider```

```
@Inject
protected JpaView(JpaContainerProvider containerProvider) {
    this.containerProvider = containerProvider;
}
```
- create a container for each persistence unit using **@DerbyJpa** and **@HsqlJpa annotation classes**, and the entity type, *Person.class*.  There is currently only one container type.

```
derbyContainer=containerProvider.get(DerbyJpa.class, Person.class, JpaContainerProvider.ContainerType.CACHED);
hsqlContainer=containerProvider.get(HsqlJpa.class, Person.class, JpaContainerProvider.ContainerType.CACHED);
```
- now we can create the Vaadin ```Table```s, using the containers to provide the data
```
derbyTable = new Table("Derby Table",derbyContainer);
hsqlTable = new Table("",hsqlContainer);
```
- complete the layout so it looks like this

```
@Override
protected void doBuild(ViewChangeBusMessage busMessage) {
    derbyContainer=containerProvider.get(DerbyJpa.class, Person.class, JpaContainerProvider.ContainerType.CACHED);
    hsqlContainer=containerProvider.get(HsqlJpa.class, Person.class, JpaContainerProvider.ContainerType.CACHED);
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
- provide the I18N captions for the ```Table``` components

```
@Caption(caption = LabelKey.Derby_Table, description = DescriptionKey.Table_connected_to_DerbyDb)
private Table derbyTable;
@Caption(caption = LabelKey.HSQL_Table, description = DescriptionKey.Table_connected_to_HsqlDb)
private Table hsqlTable;
```
- run the application just to make sure you have everything correctly set up so far.  There is no data to display yet, so all you will see is two empty tables.

#Data

- create a convenience method for creating new people
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

- inject an EntityManagerProvider for each persistence unit, using the binding annotations to identify them

```
@Inject
protected JpaView(JpaContainerProvider containerProvider, @DerbyJpa EntityManagerProvider derbyEntityManagerProvider, @HsqlJpa EntityManagerProvider hsqlEntityManagerProvider) {
    this.containerProvider = containerProvider;
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

##Dao

Of course, it is not possible to annotate a lambda, so Krail provides a generic Dao for the simple JPA calls to avoid the need for creating annotated methods.

- inject the Dao for each persistence unit

```
@Inject
protected JpaView(JpaContainerProvider containerProvider, @DerbyJpa EntityManagerProvider derbyEntityManagerProvider, @HsqlJpa EntityManagerProvider
        hsqlEntityManagerProvider, @DerbyJpa JpaDao_LongInt derbyDao, @HsqlJpa JpaDao_LongInt hsqlDao) {
    this.containerProvider = containerProvider;
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

another page


<a name="persistence-i18n"></a>
#Persistence for I18N



