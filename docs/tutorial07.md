#Introduction

You have seen some aspects of Krail's User Access Control already, and are probably aware that it provides this by integrating [Apache Shiro](http://shiro.apache.org/).  This Tutorial will not attempt to cover the whole of Shiro's capability - Shiro's own documentation does a good job of that already.  

What we will do, however, is demonstrate some of the features of Shiro, within a Krail context:

- **Implementing a Realm**. Implement a trivial Realm to provide authentication and authorisation
- **Page Control**.  This is Krail specific use of Shiro features to determine whether a user has permission to access a page
- **Coded access**.  Checking from code whether a user has permissions to do something
- **Access Control annotations**.  This will demonstrate the use of Shiro's annotations, as an alternative to using coded access

Krail does not yet provide any user management capability (the management of users, groups & roles etc) as this is often provided via LDAP, Active Directory or Identity Management systems.  There is an [open ticket](https://github.com/davidsowerby/krail/issues/226) for it, so it may one day be developed.


#Example

We will take this opportunity to tidy up our site, and limit who can use different parts of the site.  This is what we want to achieve:
 
- the 'finance' pages should be on their own branch 
- public pages will remain available to any user
- private pages will be limited to just 2 users, "eq" and "fb"
- both users will have access to the 'private' branch
- both users will be able to change their own options
- 'fb' will be able to access the finance pages, but 'eq' will not.
- there will be an 'admin' user who can access all pages and change all options

At this point we must stress that this is going to be a trivial example of User Access Control, and to do it properly you need to consult the Shiro documentation.  This Tutorial should give you some useful pointers, however.

#Move the Pages

To move the 'finance' pages:

- change the line in the ```BindingManager``` to put the ```MyPages``` root URI at *finance* instead of *private/finance-department*

```
    baseModules.add(new MyPages().rootURI("finance"));
```
- In the ```PurchasingView``` change the uri parameter to be *finance/purchasing*

```
@View(uri = "finance/purchasing", pageAccessControl = PageAccessControl.PERMISSION, labelKeyName = "Purchasing")
public class PurchasingView extends Grid3x3ViewBase {

}
```
- In ```NewsView.doBuild()``` change the button event to point to the new page location
```
navigateToPrivatePage.addClickListener(c -> navigator.navigateTo("finance/accounts"));
```


#User accounts

 - create a new package, 'com.example.tutorial.uac'
 - in that package create a new class "TrivialUserAccount" - it is obvious what it does
```
package com.example.tutorial.uac;

import java.util.Arrays;
import java.util.List;

public class TrivialUserAccount {

    private String password;
    private List<String> permissions;
    private String userId;

    public TrivialUserAccount(String userId, String password, String... permissions) {
        this.userId = userId;
        this.password = password;
        this.permissions = Arrays.asList(permissions);
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">You may notice that there is no "role" in this user account.  You can certainly use Shiro's roles in Krail, but we prefer to use permissions for the <a href="https://shiro.apache.org/authorization.html#Authorization-ElementsofAuthorization" target="">reasons given</a> by the Shiro team.</p>
</div>



#Credentials Store

- create a class ""TrivialCredentialsStore" as somewhere to keep the user accounts: 

```
package com.example.tutorial.uac;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public class TrivialCredentialsStore  {
    private Map<String, TrivialUserAccount> store = new HashMap<>();

    @Inject
    protected TrivialCredentialsStore() {
    }

    public TrivialCredentialsStore addAccount(String userId, String password, String... permissions) {
        store.put(userId, new TrivialUserAccount(userId, password, permissions));
        return this;
    }


    public TrivialUserAccount getAccount(String principal) {
        return store.get(principal);
    }
}
```
- define the users' credentials to meet our requirements - we'll just put them in the constructor
 
```
@Inject
protected TrivialCredentialsStore() {
    addAccount("eq", "eq", "page:view:private:*","option:edit:SimpleUserHierarchy:eq:0:*:*");
    addAccount("fb", "fb", "page:view:private:*","page:view:finance:*","option:edit:SimpleUserHierarchy:fb:0:*:*");
    addAccount("admin", "password", "page:view:*","option:edit:*");
}
```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">We don't need to explicitly bind TrivialCredentialsStore in a Guice module - there are <a href="https://github.com/google/guice/wiki/JustInTimeBindings" target="">occasions when Guice does not require this</a>.</p>
</div>


##Permission Strings

What we have done here is give users specific credentials.  The userId and password are obvious.  The permission strings use Shiro's ```WildcardPermission```. 

This is a very flexible way of [defining permissions](https://shiro.apache.org/permissions.html).   Krail uses the ```WildcardPermission``` to define page and option approval.
  
##Page Permission

So for example, a page with a url of:

```
private/apage/asubpage/id=1
```
is translated by Krail's ```PagePermission``` into a Shiro compatible syntax of:

```
page:view:private:apage:asubpage
```

This represents:

- resource type ('page')
- action ('view')
- resource instance (the Url with the '/' transposed to a ':' to match the Shiro syntax)
- the url parameter is ignored, because it is not part of the page definition


This is then compared, by Shiro, with the permission a user has been given.  Both 'eq' and 'fb' have been given a permission:
```
page:view:private:*
```
which translates to "for a resource type page, this user can view any with a url starting with *private*"

The 'admin' user has been given permission to view any page, simply by wildcarding all pages

```
page:view:*
```

##Option permission

An Option follows a similar pattern, provided by ```OptionPermission```

- resource type ('option')
- action ('edit')
- resource instance (an option) structured [hierarchy]:[user id]:[hierarchy level index]:[context]:[option name]:[qualifier]:[qualifier]

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">The javadoc for OptionPermission is incorrect, it misses out the userId.  See #419</p>
</div>

Thus the option permissions given to 'eq' and 'fb' enable to edit only their own options in the ```SimpleUserHierarchy```.  This is set by giving permission only for their own userId, at hierarchy level index = 0  

Again the 'admin' user is all-powerful, with permission to edit any option:

```
option:edit:*
```

#Authentication

Shiro has the concept of a ```Realm```, where the rules for Authentication and Authorisation are defined - by you, as they will be application specific.  Shiro offers a number of ways to [implement Realm](https://shiro.apache.org/static/1.2.2/apidocs/org/apache/shiro/realm/Realm.html), and here we will just provide a trivial example, combining authentication and authorisation into one ``` Realm```
 
- in the package, 'com.example.tutorial.uac' create a class "TutorialRealm", extending ```AuthorizingRealm```

```
package com.example.tutorial.uac;

import org.apache.shiro.realm.AuthorizingRealm;

public class TutorialRealm extends AuthorizingRealm {

  
}
```

- We want to use our ```TrivialCredentialsStore```, so create an instance the constructor
- Disable caching, as we do not need a cache

```
import com.example.tutorial.uac.TrivialCredentialsStore;
import com.google.inject.Inject;
import org.apache.shiro.realm.AuthorizingRealm;

public class TutorialRealm extends AuthorizingRealm {

    private TrivialCredentialsStore credentialsStore;

    @Inject
    protected TutorialRealm(TrivialCredentialsStore credentialsStore) {
        super();
        this.credentialsStore = credentialsStore;
        setCachingEnabled(false);
    }
}
```
- provide the authentication logic by overriding ```doGetAuthenticationInfo()```

```
@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    TrivialUserAccount userAccount = credentialsStore.getAccount((String) token.getPrincipal());
    if (userAccount == null) {
        return null;
    }
    String tokenCredentials = new String((char[])token.getCredentials());
    if(userAccount.getPassword().equals(tokenCredentials)) {
        return new SimpleAuthenticationInfo(userAccount.getUserId(),token.getCredentials(),"TutorialRealm");
    }else{
        return null;
    }
}
```
This logic returns null if the user account is not found, or the password supplied by the token does not match the credentials.  If authentication is successful, a populated instance of ```SimpleAuthenticationInfo``` is returned 

#Authorisation

- override ```doGetAuthorizationInfo()``` to provide the authorisation logic

```
@Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    TrivialUserAccount userAccount = credentialsStore.getAccount((String) principals.getPrimaryPrincipal());
    if (userAccount != null) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(new HashSet(userAccount.getPermissions()));
        return info;
    }
    return null;
}
```
This logic returns a populated ```SimpleAuthorizationInfo``` instance if the user account is found, or null if not 

#Using the Realm

- override the ```shiroModule()``` method in the ```BindingManager``` to use the new ```Realm```
 
```
@Override
protected Module shiroModule() {
    return new DefaultShiroModule().addRealm(TutorialRealm.class);
}
```
- run the application and check to see if we have met our requirements:
   
    - log in as 'eq', with password 'eq'
    - *private* pages should be visible, but not the *finance* pages or *system admin* pages
    - you should still be able to modify options on the "My News" page
    - pressing the "system option" button on "My News" will result in a "You do not have permission" message
    - log out
    - log in as 'fb' - try a wrong password if you like, the correct password should be 'fb'
    - *private* and *finance* pages should be visible, but not *system admin* pages
    - you should still be able to modify options on the "My News" page
    - pressing the "system option" button on "My News" will result in a "You do not have permission" message
    - log out
    - log in as 'admin', password= 'password'
    - *private*, *finance* and *system admin* pages pages should all be visible
    - you should still be able to modify options on the "My News" page
    - pressing the "system option" button on "My News" remove the CEO news

So far this has all been done using page and option permissions.  The visibility of pages is actually managed through ```PageAccessControl``` which limits what is made available to the navigation components.  You can take also direct control using code or Shiro annotations.  

#Control Access Through Code

At the moment the "system option" button on "My News" can result in a "You do not have permission" message.  Let's prevent that by not showing the button unless the user has permission.
 
- to get access to the current Shiro ```Subject```, we inject a ```SubjectProvider``` 

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">"Subject" is a Shiro term to describe any type of user</p>
</div>


<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">The Shiro documentation tells you to use SecurityUtils.getSubject() to access the current Subject.  This will not work in Krail (or any Vaadin application).  In Krail, always use SubjectProvider.get()</p>
</div>



```
@Inject
public MyNews(Option option, OptionPopup optionPopup, SubjectProvider subjectProvider) {
    this.option = option;
    this.optionPopup = optionPopup;
    this.subjectProvider = subjectProvider;
}
```



- in ```MyNews.doBuild()``` make the visibility conditional on the user having permission

```
if (subjectProvider.get().isPermitted("option:edit:SimpleUserHierarchy:*:1:*:*")) {
    systemOptionButton.setVisible(true);
}else{
    systemOptionButton.setVisible(false);
}
```        
Here we have asked Shiro to confirm permission at the most specific level, as recommended by Shiro. This permission string is checking that the user has permission to edit any option at level 1 in the ```SimpleUserHierarchy```

- run the application and log in as 'eq' or 'fb' and you will not be able to see the "system option" button.  Log in as 'admin', however, and the "system option" button is visible.
  
#Control Access Through Annotations

Shiro provides a [set of annotations](https://shiro.apache.org/java-annotations-list.html) to cover most circumstances.  We will use **@RequiresPermissions** as an example
 
- on the "MyNews" page add another button in ```doBuild()```

```
payRiseButton = new Button("request a pay rise");
payRiseButton.addClickListener(event-> requestAPayRise());
setBottomLeft(payRiseButton);
```

- inject the ```UserNotifier```

```
@Inject
public MyNews(Option option, OptionPopup optionPopup, SubjectProvider subjectProvider, UserNotifier userNotifier) {
    this.option = option;
    this.optionPopup = optionPopup;
    this.subjectProvider = subjectProvider;
    this.userNotifier = userNotifier;
}
```
- create the ```requestAPayRise``` method
- user ```userNotifier``` to give feedback
- create the enum constant **DescriptionKey.You_just_asked_for_a_pay_increase**

```

protected void requestAPayRise() {
    userNotifier.notifyInformation(DescriptionKey.You_just_asked_for_a_pay_increase);
}
```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">Guice AOP <a href="https://github.com/google/guice/wiki/AOP#limitations" target="">will not work on private or final methods</a> - it is easy to forget that, especially as your IDE may auto create the method as private.  If a method annotation does not work as expected, that is the first thing to check</p>
</div>

- We want to restrict who can use the method, so we will annotate it with a new permission

```
@RequiresPermissions("pay:request-increase")
protected void requestAPayRise() {
    userNotifier.notifyInformation(DescriptionKey.You_just_asked_for_a_pay_increase);
}
```

Nobody currently has permission to do this, so let's allow user 'eq' to do this

- modify the entry for 'eq' in ```TrivialCredentialsStore``` to add this permission

```
addAccount("eq", "eq", "page:view:private:*","option:edit:SimpleUserHierarchy:eq:0:*:*","pay:request-increase");
```

- run the application
    - log in as 'eq'
    - navigate to "My News" and press "request a pay rise".
    - A notification pops up to confirm the request.  (Unfortunately it doesn't say what will happen to the request)
    - log in as 'fb' or 'admin'
    - navigate to "My News" and press "request a pay rise".
    - you receive a "not permitted" message
    
#Summary

We have:
- Shown how to control access to pages
- Shown how access control is applied to Options
- Shown how to control access using code, or annotations
- Built a very simple credential store with user accounts
- Demonstrated some uses of Shiro's Wildcard permissions

#Download from Github
To get to this point straight from Github, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step07**