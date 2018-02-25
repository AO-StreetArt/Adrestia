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

``sudo ./scripts/docker/elk/copyKibanaConfig.sh``


``docker-compose up``

The Kibana UI will be available at http://127.0.0.1:5601, and logs will be available there.  Note that logs sent prior to the logstash endpoint being available will not be seen.

To stand up only Adrestia's dependencies, without standing up Adrestia itself,
run the following from the 'scripts/deps' folder of the repository:

``docker-compose up``

In both cases, the following UI's will be available:

Consul - http://localhost:8500/ui
Neo4j - http://localhost:7474

I recommend `MongoDB Compass <https://www.mongodb.com/products/compass>__`, Community Edition for a Mongo UI, which can connect on localhost:27017



Building from Source
--------------------

Once you've got the required backend services started, build and execute the tests
for the repository.  Please note that integration tests will fail unless you
have instances of the required backend services running:

``gradle check``

And, finally, start Adrestia:

``gradle bootRun``

Using the Latest Release
------------------------

Adrestia can also be downloaded as a runnable JAR for the latest release from `here <https://github.com/AO-StreetArt/Adrestia/releases>`__.

When using a JAR, unzip the downloaded package, move to the main directory from a terminal, and run:

``java -jar build/libs/adrestia-0.1.0.jar``
