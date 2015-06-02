#Introduction

Krail sees Options as the [top layer of configuration](devguide01.md).  The idea of options is to give users as much control as the Krail developer wants to give them, at runtime - so that could be changing the way a screen is laid out, what appears on the scrren - anyhitng which might typically be in a user menu of options or settings. 

#User Hierarchy
Krail takes the idea of options a step further than simply letting the user choose a value for something - although it does that.

Imagine you have a system which provides information to staff - but that information may vary depending on, say, their location.  You could quite easily, just let the user select which area's information they see.  That is not a bad idea, but you also want to set some sensible defaults, so that when the system is launched, the users see something relevant to them.

So let's assume we have two users

- Emily Quick, a developer who works in Sheffield, England
- Franck Baton, an accountant who works in Finance, in Frankfurt, Germany 

When you think about their locations and how that relates to information, you can imagine a useful hierarchy of:
 
- Europe, UK, Sheffield for Emily
- Europe, Germany, Frankfurt for Franck

Emily & Franck would share the Europe level information, and have more relevant information for their specific locations.

Krail requires two other levels for ALL UserHierarchy implementations, so the full hierarchies would be:

- **system**, Europe, UK, Sheffield, **user** for Emily
- **system**, Europe, Germany, Frankfurt, **user** for Franck

This simply means that the system level option affects the whole system, and the user level is specific to an individual user.

Now - starting from the system level option, imagine that each subsequent level overrides the previous one, so that the user level is the highest "rank".  A system admin could set up an option as a default for the whole system - a European admin to set a different default for Europe to distinguish from the US, a Germany admin to set a differnt value again specifically for Germany.  Ultimately, however, a user selects their own (if you give them access to do so!) 

That's a lot of explanation so let's start something practical with the Krail default hierarchy:

#SimpleHierarchy

Krail provides a default, two level ```UserHierarchy``` called ```SimpleHierarchy```, comprising just the system and user level. We will demonstrate its use by providing our users with their own news page, wehre they can select which news channels they see.

- 

A short explanation - runtime configurable

create MyNews
create 3 text components - CEO announce, Vacancies & Items for Sale; personal status label 
boolean visible option for each 
default true for first 2

show it

checkboxes and textbox to change at system and individual level, including set to null and set system option vacancies to false <<---- POPUP??




















#Introduction

The idea of providing users with options is a standard requirement for many applications, whether it is just letting them decide what they see on a page, or maybe the news feed they get.  Krail provides an implementation which should be flexible enough for any application, with a minimum of effort.  This guide describes the structure and principles behind Options - for detail of how to use them, please refer to the [Tutorial](tutorial05.md).

#Relationship to Configuration

Krail sees ```Option``` as the final layer of configuration.  In practice, what matters is that the Krail developer has a huge amount of flexibility and control in managing configuration, including users' individual options.

#Layers of Options

At its simplest, a user should be able to select and save options, then retrieve them from persistence next time they use the system.  The user may not have used the system before, though, so we need some defaults to start with. In Krail these defaults are provided in code, as the ```Option.get()``` method requires a default value - this also ensures that behaviour is predictable if option values are missing.

So we have a user defined value for an option and a coded default.  But now suppose we think it would be better if we could change some options for all users - "system options" in effect.  Or to make things a bit more complicated, we want to set some options at system level, and allow users to override just some of them.  

This is nothing more than a simple hierarchy, represented in Krail by ```UserHierarchy```.  If we simply say that values at the user level override those at system level, then we almost have what we want.  And only allowing authorised users to change some of the ```Option values```, those become system level options.  So for this simple, 2 level hierarchy, the logic for retrieving an option value is quite simply to take the first non-null value we find from the following order:

user level
system level
coded default

#Controlling the Options

Of course, you don't have to give all users the facility to change all options - you may restrict changing some options values, for example, to sys admins, to provide consistency across the whole system.

Accessing options is always through the Option interface.  This enables a simple, consistent API for storing and retrieving options.

#Hierarchies

What has been described above is a simple, 2 layer, ```UserHierarchy``` implementation, and this is the default provided by Krail.  But that may not be enough for you.  Perhaps you are developing an application for a large, complex organisation, and what you would really like to do is have layers like those described above, but structured by geography or company structure - or maybe both.

That is easily achievable with your own implementation of ```OptionLayerDefinition```.  This interface has a method which returns a list (an ordered hierarchy)  based on parameters of user and hierarchy name.  For a specific user this may return "London, Europe" for geography, and "Engineering, Automotive, Off Road" for company structure - the data for these would probably both be obtained from another corporate system.

The principles described above remain the same, however - so for this example, ```Option``` will return the first non-null value found from a location hierarchy of:
  

user
city
continent
system
coded default

There can be up to 98 layers between user and system levels, though we can think of no sane reason for wanting that many.

#Storing the Options

None of this is of any use unless the option values can be stored.  As with the rest of Krail, an interface, ```OptionStore```, and a default implementation are provided.  In this case, the default implementation is not very useful, as it only store the options in memory.  A persistent version is planned, but in the meantime you could provide your own persistent implementation and bind it through a sub-class of OptionModule.

#OptionKey
