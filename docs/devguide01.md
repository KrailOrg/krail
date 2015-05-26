#Krail Configuration - the Big Picture

##Objective
As it says at the start of this Tutorial, the objective is to give you, the Krail developer, the best of both worlds - quick results, but still the freedom to modify things however you wish.  Of course, as with any form of freedom, there is also responsibility.  So if you break something, you can guess who will be expected to fix it !

##Configuration levels
Configuration is possible at multiple levels, and how you use them is largely up to you.

###Level 0 - Requires a re-compile

When making fundamental changes - for example using a different implementation for a specific interface - reconfiguration is through Guice modules.  As these are in code, this will of course require a recompile.  Guice annotations also play a part in this.  Krail is programmed almost entirely to interfaces, so at this level of configuration you could change just about anything.  Or, indeed, break just about anything.  Also in this category is the use of the EventBus, since you can choose to accept or send messages and act upon them as required.  You will see examples of this throughout the Tutorial.

###Level 1 - Loadable configuration

Even in small applications, there are times when it is inconvenient to require a recompile in order to change system behaviour.  Krail integrates [Apache Commons Configuration](https://commons.apache.org/proper/commons-configuration), which provides support for many formats for loading configuration. Krail uses the extended properties file format itself (basically an ini file with sections).  It is also possible to merge multiple inputs, thus supporting a modular approach if you build a Krail application with multiple libraries or modules.  It is up to you to ensure the files are loaded when needed, but not unnecessarily often.  There will be at least one example of this in the Tutorial.

###Level 2 - Dynamic options

A further level of configuration is provided through the ```Option class```.  This enables the update of options as the application is running - it is up to the application to dynamically update if that is required.  This configuration is also multi-layered, so that there is the potential, for example, to have options set at system, department and individual user level (Krail does not determine this structure of this hierarchy, as that it application dependent, it simply provides a mechanism to enable it). Option is typically used to allow users to make their own choices, but also provide typical defaults based on one or more hierarchies they are a member of.  This is quite a large subject and therefore has its own section, [Option and UserHierarchy](tutorial05.md).
 
