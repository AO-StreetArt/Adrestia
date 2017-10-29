.. _quickstart:

Getting Started with Adrestia
=============================

:ref:`Go Home <index>`

Docker
------

Adrestia depends on a number of other services, a full list of which can be found at `The Aesel Component List <http://aesel.readthedocs.io/en/latest/pages/components.html>`__.

Adrestia provides several `Docker Compose <https://docs.docker.com/compose/>`__ scripts to stand up dependencies automatically,
which are especially valuable for experimenting with the server and/or setting up
development environments.

To stand up all of Adrestia's dependencies, as well as the Adrestia Docker image,
run the following from the main folder of the repository:

``docker-compose up``

To stand up only Adrestia's dependencies, without standing up Adrestia itself,
run the following from the 'scripts/deps' folder of the repository:

``docker-compose up``

Building from Source
--------------------

Once you've got the required backend services started, build and execute the tests
for the repository.  Please note that integration tests will fail unless you
have instances of the required backend services running:

``gradle check``

And, finally, start Adrestia:

``gradle bootRun``
