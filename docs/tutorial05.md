#Options and User Hierarchies

to be written


Script something like:

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
