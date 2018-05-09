=======
Testing
=======

Introduction
============

This pages captures some tips and techniques to assist you in testing
your application.

ResourceUtils
=============

The ResourceUtils class is part of the core, and is used to look up
various directories, and can be used to manipulate the environment when
testing. In the VaadinService example you can see that it is used to
retrieve the user’s home directory, but what is not immediately obvious
is that is also used to determine the application base directory and
configuration directory, and these are derived from the VaadinService.
If you have mocked the VaadinService, as described, then you can set up
application configuration however you wish for testing.

VaadinService
=============

You will often find that your test needs a VaadinService to run, but of
course is not usually available in a test environment - unless you are
running full functional testing. To overcome this, we mock the service,
with the help of ResourceUtils like this:

::

    static VaadinService vaadinService;

    @BeforeClass
    public static void setupClass() {
       vaadinService = mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(ResourceUtils.userTempDirectory());
        VaadinService.setCurrent(vaadinService);
    }
