.. _quickstart:

Getting Started with CLyman
===========================

:ref:`Go Home <index>`

Building from Source
--------------------

Building Adrestia from source is quite simple, but it will require several running services
to exist on the machine prior to startup.  Please see `The Aesel Component List <http://aesel.readthedocs.io/en/latest/pages/components.html>`__
for a full list to all required services, and documentation for each.

Connections to CLyman and Ceph are not yet implemented, so are not yet required.
Follow the `Crazy Ivan Docker instructions <http://crazyivan.readthedocs.io/en/latest/pages/quickstart.html#docker>`__ to get started.

Once you've got the required backend services started, clone the Adrestia repository:

``git clone https://github.com/AO-StreetArt/Adrestia.git``

Build and execute the tests for the repository:

``cd adrestia && gradle check``

And, finally, start Adrestia:

``gradle bootRun``
