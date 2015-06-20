#Introduction

JPA support for Krail is provided by the [krail-jpa[(https://github.com/davidsowerby/krail-jpa) library, which in turn is mostly provided by [Apache Onami Persist](https://onami.apache.org/persist/).  This was chosen in preference to [guice-persist](https://github.com/google/guice/wiki/GuicePersist), primarily for its ability to support multiple concurrent database instances.  

A useful comparison  of Onami Persist and Guice Persist can be found [here](https://onami.apache.org/persist/guicePersist.html).

The Krail library also provides a slightly modified JPAContainer - also to enable support for multiple instances.  It also provides DAO implementations for krail core (for Option and I18N Patterns)

An reasonable understanding of JPA is assumed.

#Example

We will:
 
- create a page, 
- configure two database connections (one HSQLDB and One Apache Derby), 
- demonstrate CRUD on a sample entity
- demonstrate the use of JPAContainer with two databases, to provide Tables
- demonstrate integration with Krail I18N and ```Option```
 
#Prepare build

- include krail-jpa in the build, by adding it to *build.gradle* dependencies
- remove the javax persistence dependency (no longer needed, as this is provided by krail-jpa)
```groovy
dependencies {
    compile 'uk.q3c.krail:krail:0.9.6'
    compile 'uk.q3c.krail:krail-jpa:0.8.10'
}
```
 
#Create a Page

If you have followed the whole Tutorial, you will be an expert at this by now
 
- add a page to ```MyOtherPages```

```
addEntry("jpa", JPAView.class, LabelKey.JPA, PageAccessControl.PUBLIC);
```
- create JPAView in package 'com.example.tutorial.pages', extended from ```ViewBase```

```
package com.example.tutorial.pages;

import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class JPAView extends ViewBase {
    
    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        
    }
}

```
#Configure connections

This is one occasion where it is more desirable to sub-class the relevant Guice module - there is a lot that can be configured for a connection.

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
- create the 'DerbyJpa' and 'HsqlJpa' annotations - these are Guice Binding Annotations (denoted by **@BindingAnnotation**), which will enable you to select which connection you want to use from within the application.

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
- create a temporary folder for our Derby database - we will use the module constructor, not a recommended approach for production!

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
- Unfortunately we still need a minimal persistence.xml file, so we need to
       - create folder src/main/resources/META-INF
       - create the following persistence.xml file in that folder

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

<a name="persistence-i18n"></a>
#Persistence for I18N