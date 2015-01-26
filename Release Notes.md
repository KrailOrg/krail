### Release Notes for krail 0.7.8

This version moves most of the build to the [master project](https://github.com/davidsowerby/krail-master)

#### Change log

   [309](https://github.com/davidsowerby/krail/issues/309): Automate build


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.5
   test compile dependency version changed to: q3c-testUtil:0.7.2

#### Detail

Fix [309](https://github.com/davidsowerby/krail/issues/309)  Build automated, most in Gradle custom tasks

The custom tasks are provided by the q3c-gradle project. There are tasks for verification, prepareRelease and Release.  These have been built in to a re-built multi-project structure with krail-master as the master project



