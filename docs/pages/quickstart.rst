.. _quickstart:

Getting Started with Adrestia
=============================

:ref:`Go Home <index>`

Adrestia is primarily a Reverse Proxy server, which means that without it's
dependent services (ie. Crazy Ivan and CLyman), it does not do much.  For this
reason, Docker is the recommended method of experimenting with Adrestia, as it
requires minimal effort to get a basic, development cluster standing up.

Regardless of how you start Adrestia and it's dependent services, the easiest
way to explore the API currently provided by the cluster is by using
`Postman <https://www.getpostman.com/>`__.  You'll find an environment and
collection JSON in the src/test/json folder, which can be imported directly
into your instance of Postman.

Docker
------

Adrestia depends on a number of other services, a full list of which can be found at `The Aesel Component List <http://aesel.readthedocs.io/en/latest/pages/components.html>`__.

Adrestia provides several `Docker Compose <https://docs.docker.com/compose/>`__ scripts to stand up dependencies automatically,
which are especially valuable for experimenting with the server and/or setting up
development environments.

To stand up all of Adrestia's dependencies, as well as the Adrestia Docker image,
run the following from the 'scripts/linux' folder:

``./start_dev_cluster.sh``

The following Web UI's will be available in your browser:

Consul - http://localhost:8500/ui
Neo4j - http://localhost:7474

I recommend `MongoDB Compass <https://www.mongodb.com/products/compass>__`, Community Edition for a Mongo UI, which can connect on localhost:27017

Building from Source
--------------------

Once you've got the required backend services started, build and execute the tests
for the repository.  Please note that integration tests will fail unless you
have instances of the required backend services running:

``./gradlew check``

And, finally, start Adrestia:

``./gradlew bootRun``

Using the Latest Release
------------------------

Adrestia can also be downloaded as a runnable JAR for the latest release from `here <https://github.com/AO-StreetArt/Adrestia/releases>`__.

When using a JAR, unzip the downloaded package, move to the main directory from a terminal, and run:

``java -jar build/libs/adrestia-0.2.0.jar``
