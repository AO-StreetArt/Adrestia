Adrestia
========

.. figure:: https://travis-ci.org/AO-StreetArt/Adrestia.svg?branch=master
   :alt:

Overview
--------

Adrestia is the Gateway Service for the Aesel Distributed Visualization platform.

An Aesel cluster consists of:

* One or more instances of CLyman
* One or more instances of Crazy Ivan
* A Mongo Cluster

Different clusters manage disparate scenes (or groups of renderable objects),
and Adrestia abstracts away these clusters into a single transactional plane,
which can be accessed by typical transactional services, and end users.  It
can be scaled horizontally, and runs against a Mongo Cluster itself.

Adrestia also forms a security layer on top of it's dependent services.  It provides
a number of additional HTTP security features, including a full-scale authentication
flow powered by Auth0.

Full Documentation for Adrestia is available `here <http://adrestia.readthedocs.io/en/v2/>`__.
Repository for Adrestia is available at `here <https://github.com/AO-StreetArt/Adrestia>`__.

Full Documentation for the Aesel Distributed Visualization Platform, including the HTTP API
for Adrestia, can be found `here <http://aesel.readthedocs.io/en/latest/>`__.

Features
--------

- Edge Proxy for HTTP communications with end users
- Authentication supported by Auth0 integration
- Cluster-based routing driven by Scenes (data stored in Crazy Ivan)
- Internal Service mesh for transactional services to communicate with Aesel clusters

Adrestia is a part of the AO Aesel Project, along with
`Crazy Ivan <https://github.com/AO-StreetArt/CrazyIvan>`__
& `CLyman <https://github.com/AO-StreetArt/CLyman>`__.
