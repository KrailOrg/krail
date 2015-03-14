This version introduces an Event Bus and replaces the event listener type of Observer pattern used previously, with Publish Subscribe.  All Krail observer patterns are now implemented using the Event Bus.

This should be the last major change to the API.

The MasterSitemapNode is now immutable, and some scope corrections made.  There are likely still to be some changes around allocating scope correctly and ensuring Krail is tread-safe in the right places.